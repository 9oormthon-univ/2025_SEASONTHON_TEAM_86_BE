package com.goorm.derere.controller;

import com.goorm.derere.entity.Survey;
import com.goorm.derere.entity.SurveyAnswer;
import com.goorm.derere.entity.SurveyOption;
import com.goorm.derere.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    // 설문조사 전체 목록 조회 (사용자)
    @GetMapping
    public List<Survey> getSurveys() {
        return surveyService.getAllSurveys();
    }

    // 설문조사 옵션 조회 (사용자)
    @GetMapping("/{surveyId}/options")
    public List<SurveyOption> getOptions(@PathVariable Integer surveyId) {
        return surveyService.getOptionsBySurveyId(surveyId);
    }

    // 설문 응답 저장 (사용자)
    @PostMapping("/answers/batch")
    public List<SurveyAnswer> createAnswers(@RequestBody List<SurveyAnswer> answers) {
        return surveyService.saveAnswers(answers);
    }


    //특정 설문에 대한 내 응답 조회 (사용자)
    @GetMapping("/answers/{userid}/restaurant/{restaurantId}")
    public ResponseEntity<List<SurveyAnswer>> getUserAnswersByRestaurant(@PathVariable Long userid, @PathVariable Long restaurantId) {
        List<SurveyAnswer> answers = surveyService.getAnswersByUserAndRestaurant(userid, restaurantId);
        if (answers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(answers);
    }

    // 설문 응답 수정 (사용자)
    @PutMapping("/answers/batch")
    public List<SurveyAnswer> updateAnswers(@RequestBody List<SurveyAnswer> answers) {
        return surveyService.updateAnswers(answers);
    }

    // 설문 응답 삭제 (사용자)
    @DeleteMapping("/answers/{userid}/restaurant/{restaurantId}")
    public ResponseEntity<Void> deleteAnswersByUserAndRestaurant(@PathVariable Long userid, @PathVariable Long restaurantId) {
        surveyService.deleteAnswersByUserAndRestaurant(userid, restaurantId);
        return ResponseEntity.noContent().build();
    }


    // 점주: 설문조사 통계 조회
    @GetMapping("/{surveyId}/stats")
    public Map<Integer, Long> getSurveyStats(@PathVariable Integer surveyId) {
        return surveyService.getSurveyStats(surveyId);
    }
}

