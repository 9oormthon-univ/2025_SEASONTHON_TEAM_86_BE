package com.goorm.derere.dto;

import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.entity.RestaurantType;
import lombok.Getter;

@Getter
public class RestaurantResponse {

    private final Long restaurantId;
    private final String restaurantName;
    private final String restaurantInfo;
    private final RestaurantType.TypeName restaurantType;
    private final String restaurantNum;
    private final String restaurantAddress;
    private final String restaurantTime;
    private final Integer restaurantVote;
    private final Integer restaurantLike;
    private final String ownerUsername;
    private final Long ownerId;
    private final String restaurantImageUrl;

    public RestaurantResponse(Restaurant restaurant) {
        this.restaurantId = restaurant.getRestaurantId();
        this.restaurantName = restaurant.getRestaurantName();
        this.restaurantInfo = restaurant.getRestaurantInfo();
        this.restaurantType = restaurant.getRestaurantType().getTypeName();
        this.restaurantNum = restaurant.getRestaurantNum();
        this.restaurantAddress = restaurant.getRestaurantAddress();
        this.restaurantTime = restaurant.getRestaurantTime();
        this.restaurantVote = restaurant.getRestaurantVote();
        this.restaurantLike = restaurant.getRestaurantLike();
        this.ownerUsername = restaurant.getUser().getUsername();
        this.ownerId = restaurant.getUser().getUserid();
        this.restaurantImageUrl = restaurant.getRestaurantImageUrl();
    }
}