package com.goorm.derere.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 좋아요 요청 DTO
@Getter
@Setter
@NoArgsConstructor
public class LikeRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "좋아요 상태는 필수입니다.")
    private Boolean liked;
}