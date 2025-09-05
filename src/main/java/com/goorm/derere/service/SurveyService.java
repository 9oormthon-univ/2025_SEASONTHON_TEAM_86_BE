package com.goorm.derere.service;

import com.goorm.derere.entity.Survey;
import com.goorm.derere.entity.SurveyAnswer;
import com.goorm.derere.entity.SurveyOption;
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

    // 설문조사 전체 조회 (사용자용)
    public List<Survey> getAllSurveys() {
        return surveyRepo.findAll();
    }

    // 특정 설문조사의 옵션 조회 (사용자용)
    public List<SurveyOption> getOptionsBySurveyId(Integer surveyId) {
        return optionRepo.findBySurveyId(surveyId);
    }

    // 설문 응답 저장 (사용자용)
    public List<SurveyAnswer> saveAnswers(List<SurveyAnswer> answers) {
        return answerRepo.saveAll(answers);
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
        answerRepo.deleteByUseridAndRestaurantId(userid, restaurantId);
    }

    // 점주용 통계: 각 설문항목별 응답 수 통계 조회
    public Map<Integer, Long> getSurveyStats(Integer surveyId) {
        List<SurveyAnswer> answers = answerRepo.findBySurveyId(surveyId);

        return answers.stream()
                .filter(a -> a.getOptionId() != null)
                .collect(Collectors.groupingBy(
                        SurveyAnswer::getOptionId,
                        Collectors.counting()
                ));
    }
}
