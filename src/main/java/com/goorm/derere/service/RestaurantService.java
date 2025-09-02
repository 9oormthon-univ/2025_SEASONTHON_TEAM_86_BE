package com.goorm.derere.service;

import com.goorm.derere.dto.AddRestaurantRequest;
import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Transactional
    public Restaurant addRestaurant(AddRestaurantRequest addRestaurantRequest) {

        var restaurant = new Restaurant(
                addRestaurantRequest.getRestaurantName(),
                addRestaurantRequest.getUserId(),
                addRestaurantRequest.getRestaurantInfo(),
                addRestaurantRequest.getRestaurantType(),
                addRestaurantRequest.getRestaurantNum(),
                addRestaurantRequest.getRestaurantAddress(),
                addRestaurantRequest.getRestaurantTime()
        );
        return restaurantRepository.save(restaurant);
    }
}
