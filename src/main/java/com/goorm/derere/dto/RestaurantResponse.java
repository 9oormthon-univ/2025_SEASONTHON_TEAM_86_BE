package com.goorm.derere.dto;

import com.goorm.derere.entity.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantResponse {

    private final Long restaurantId;
    private final String restaurantName;
    private final String restaurantInfo;
    private final String restaurantType;
    private final String restaurantNum;
    private final String restaurantLocation;
    private final String restaurantStartTime;
    private final String restaurantEndTime;
    private final Integer restaurantVote;
    private final Integer restaurantLike;
    private final boolean restaurantOpen;
    private final String restaurantImageUrl;
    private final String ownerName;
    private final Long ownerId;
    private final int menuCount;

    public RestaurantResponse(Restaurant restaurant) {
        this.restaurantId = restaurant.getRestaurantId();
        this.restaurantName = restaurant.getRestaurantName();
        this.restaurantInfo = restaurant.getRestaurantInfo();
        this.restaurantType = restaurant.getRestaurantType().getTypeName().getDisplayName();
        this.restaurantNum = restaurant.getRestaurantNum();
        this.restaurantLocation = restaurant.getRestaurantLocation();
        this.restaurantStartTime = restaurant.getRestaurantStartTime();
        this.restaurantEndTime = restaurant.getRestaurantEndTime();
        this.restaurantVote = restaurant.getRestaurantVote();
        this.restaurantLike = restaurant.getRestaurantLike();
        this.restaurantOpen = restaurant.isRestaurantOpen();
        this.restaurantImageUrl = restaurant.getRestaurantImageUrl();
        this.ownerName = restaurant.getUser().getUsername();
        this.ownerId = restaurant.getUser().getUserid();
        this.menuCount = restaurant.getMenuCount();
    }
}