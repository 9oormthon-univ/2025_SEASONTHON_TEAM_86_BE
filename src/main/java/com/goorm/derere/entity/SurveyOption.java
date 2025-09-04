package com.goorm.derere.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class SurveyOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer option_id;
    private Long survey_id;
    private String option_text;
}
