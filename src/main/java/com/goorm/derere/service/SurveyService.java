package com.goorm.derere.service;

import com.goorm.derere.entity.Survey;
import com.goorm.derere.entity.SurveyAnswer;
import com.goorm.derere.entity.SurveyOption;
import com.goorm.derere.repository.RestaurantRepository;
import com.goorm.derere.repository.SurveyAnswerRepository;
import com.goorm.derere.repository.SurveyOptionRepository;
import com.goorm.derere.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SurveyService {
    @Autowired
    private SurveyRepository surveyRepo;
    @Autowired
    private SurveyOptionRepository optionRepo;
    @Autowired
    private SurveyAnswerRepository answerRepo;
    @Autowired
    private RestaurantRepository restaurantRep;

    // 설문조사 전체 조회 (사용자용)
    public List<Survey> getAllSurveys() {
        return surveyRepo.findAll();
    }

    // 특정 설문조사의 옵션 조회 (사용자용)
    public List<SurveyOption> getOptionsBySurveyId(Integer surveyId) {
        return optionRepo.findBySurveyId(surveyId);
    }

    // 설문 응답 저장 (사용자용)
    @Transactional
    public List<SurveyAnswer> saveAnswers(List<SurveyAnswer> answers) {
        List<SurveyAnswer> savedAnswers = answerRepo.saveAll(answers);

        // restaurant테이블에 수 저장
        if (!answers.isEmpty()) {
            Long restaurantId = answers.get(0).getRestaurantId(); // 첫 응답의 restaurantId 사용
            restaurantRep.findById(restaurantId).ifPresent(restaurant -> {
                restaurant.setRestaurantVote(restaurant.getRestaurantVote() + 1);
                restaurantRep.save(restaurant);
            });
        }

        return savedAnswers;
    }

    // 특정 사용자가 응답한 설문 조회 (사용자용)
    public List<SurveyAnswer> getAnswersByUserAndRestaurant(Long userid, Long restaurantId) {
        return answerRepo.findByUseridAndRestaurantId(userid, restaurantId);
    }

    // 설문 응답 수정 (사용자용)
    public List<SurveyAnswer> updateAnswers(List<SurveyAnswer> updatedAnswers) {
        return answerRepo.saveAll(updatedAnswers);
    }

    // 설문 응답 삭제 (사용자용)
    @Transactional
    public void deleteAnswersByUserAndRestaurant(Long userid, Long restaurantId) {
        // 응답 삭제
        answerRepo.deleteByUseridAndRestaurantId(userid, restaurantId);
        //restaurant 테이블에 수 저장
        restaurantRep.findById(restaurantId).ifPresent(restaurant -> {
            int currentVotes = restaurant.getRestaurantVote();
            restaurant.setRestaurantVote(Math.max(0, currentVotes - 1));
            restaurantRep.save(restaurant);
        });
    }


    // 각 설문항목별 응답 수 통계 조회 (점주용)
    public Map<Integer, Long> getSurveyStats(Integer surveyId) {
        List<SurveyAnswer> answers = answerRepo.findBySurveyId(surveyId);

        return answers.stream()
                .filter(a -> a.getOptionId() != null)
                .collect(Collectors.groupingBy(
                        SurveyAnswer::getOptionId,
                        Collectors.counting()
                ));
    }

    // 음식점별 투표 수 조회
    public Long getVoteCountByRestaurant(Long restaurantId) {
        Integer vote = restaurantRep.findVoteCountByRestaurantId(restaurantId);
        return vote != null ? vote.longValue() : 0L;
    }
}
