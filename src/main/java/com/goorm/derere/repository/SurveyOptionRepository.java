package com.goorm.derere.repository;

import com.goorm.derere.entity.SurveyOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyOptionRepository extends JpaRepository<SurveyOption, Integer> {
    List<SurveyOption> findBySurveyId(Integer surveyId);
}
