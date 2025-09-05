package com.goorm.derere.dto;

import lombok.Getter;

// 좋아요 응답 DTO
@Getter
public class LikeResponse {

    private final Long likeId;
    private final Long userId;
    private final Long restaurantId;
    private final String restaurantName;
    private final Boolean liked;
    private final Integer currentLikeCount;
    private final String message;

    public LikeResponse(Long likeId, Long userId, Long restaurantId, String restaurantName,
                        Boolean liked, Integer currentLikeCount, String message) {
        this.likeId = likeId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.liked = liked;
        this.currentLikeCount = currentLikeCount;
        this.message = message;
    }
}