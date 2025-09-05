package com.goorm.derere.dto;

import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.entity.RestaurantType;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RestaurantDetailResponse {

    private final Long restaurantId;
    private final String restaurantName;
    private final String restaurantInfo;
    private final RestaurantType.TypeName restaurantType;
    private final String restaurantNum;
    private final String restaurantAddress;
    private final String restaurantStartTime;
    private final String restaurantEndTime;
    private final Integer restaurantVote;
    private final Integer restaurantLike;
    private final String ownerUsername;
    private final Long ownerId;
    private final String restaurantImageUrl;
    private final List<RestaurantMenuResponse> menus;

    public RestaurantDetailResponse(Restaurant restaurant) {
        this.restaurantId = restaurant.getRestaurantId();
        this.restaurantName = restaurant.getRestaurantName();
        this.restaurantInfo = restaurant.getRestaurantInfo();
        this.restaurantType = restaurant.getRestaurantType().getTypeName();
        this.restaurantNum = restaurant.getRestaurantNum();
        this.restaurantAddress = restaurant.getRestaurantAddress();
        this.restaurantStartTime = restaurant.getRestaurantStartTime();
        this.restaurantEndTime = restaurant.getRestaurantEndTime();
        this.restaurantVote = restaurant.getRestaurantVote();
        this.restaurantLike = restaurant.getRestaurantLike();
        this.ownerUsername = restaurant.getUser().getUsername();
        this.ownerId = restaurant.getUser().getUserid();
        this.restaurantImageUrl = restaurant.getRestaurantImageUrl();
        this.menus = restaurant.getMenus().stream()
                .map(RestaurantMenuResponse::new)
                .collect(Collectors.toList());
    }
}