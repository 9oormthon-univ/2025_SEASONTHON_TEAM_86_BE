package com.goorm.derere.repository;

import com.goorm.derere.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Integer> {
    List<SurveyAnswer> findByUserid(Long userid);
    void deleteByUseridAndRestaurantId(Long userid, Long restaurantId);
    List<SurveyAnswer> findBySurveyId(Integer surveyId);
    List<SurveyAnswer> findByUseridAndRestaurantId(Long userid, Long restaurantId);
}
