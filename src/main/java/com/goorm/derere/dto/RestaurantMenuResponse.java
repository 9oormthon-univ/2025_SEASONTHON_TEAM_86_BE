package com.goorm.derere.dto;

import com.goorm.derere.entity.RestaurantMenu;
import lombok.Getter;

@Getter
public class RestaurantMenuResponse {

    private final Long restaurantMenuId;
    private final String restaurantName;
    private final String menuName;
    private final Integer menuPrice;
    private final String menuInfo;

    public RestaurantMenuResponse(RestaurantMenu menu) {
        this.restaurantMenuId = menu.getRestaurantMenuId();
        this.restaurantName = menu.getRestaurant().getRestaurantName();
        this.menuName = menu.getMenuName();
        this.menuPrice = menu.getMenuPrice();
        this.menuInfo = menu.getMenuInfo();
    }
}