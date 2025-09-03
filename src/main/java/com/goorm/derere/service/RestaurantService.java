package com.goorm.derere.service;

import com.goorm.derere.dto.AddRestaurantRequest;
import com.goorm.derere.dto.UpdateRestaurantRequest;
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

    @Transactional
    public void deleteRestaurant(Long restaurantId, Long userId) {
        long result = restaurantRepository.deleteByRestaurantIdAndUserId(restaurantId, userId);
        if (result == 0) throw new IllegalArgumentException("삭제 권한 혹은 해당 음식점이 없습니다.");
    }

    @Transactional
    public void updateRestaurant(Long restaurantId, Long userId, UpdateRestaurantRequest updateRestaurantRequest) {

        var restaurant = restaurantRepository.findByRestaurantIdAndUserId(restaurantId, userId).orElseThrow(() -> new IllegalArgumentException("해당 음식점이 없습니다."));

        // null이 아닌 값만 반영 (부분 수정)
        if (updateRestaurantRequest.getRestaurantName()    != null) restaurant.changeName(updateRestaurantRequest.getRestaurantName());
        if (updateRestaurantRequest.getRestaurantInfo()    != null) restaurant.changeInfo(updateRestaurantRequest.getRestaurantInfo());
        if (updateRestaurantRequest.getRestaurantType()    != null) restaurant.changeType(updateRestaurantRequest.getRestaurantType());
        if (updateRestaurantRequest.getRestaurantNum()     != null) restaurant.changeNum(updateRestaurantRequest.getRestaurantNum());
        if (updateRestaurantRequest.getRestaurantAddress() != null) restaurant.changeAddress(updateRestaurantRequest.getRestaurantAddress());
        if (updateRestaurantRequest.getRestaurantTime()    != null) restaurant.changeTime(updateRestaurantRequest.getRestaurantTime());
    }
}
