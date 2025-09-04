package com.goorm.derere.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long survey_id;
    private String survey_text;
}
