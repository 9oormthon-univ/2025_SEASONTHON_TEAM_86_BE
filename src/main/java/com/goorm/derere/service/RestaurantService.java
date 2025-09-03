package com.goorm.derere.service;

import com.goorm.derere.dto.AddRestaurantRequest;
import com.goorm.derere.dto.UpdateRestaurantRequest;
import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    // 전체 조회
    @Transactional(readOnly = true)
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    // 좋아요 내림차순 정렬
    @Transactional(readOnly = true)
    public List<Restaurant> getAllRestaurantsOrderByLike() {
        return restaurantRepository.findAllByOrderByRestaurantLikeDesc();
    }

    // 좋아요 TOP 1 음식점
    @Transactional(readOnly = true)
    public Restaurant getTop1RestaurantByLike() {
        return restaurantRepository.findTop1ByOrderByRestaurantLikeDesc()
                .orElseThrow(() -> new IllegalArgumentException("좋아요 TOP 1 음식점이 없습니다."));
    }

    // 좋아요 TOP 3 음식점
    @Transactional(readOnly = true)
    public List<Restaurant> getTop3RestaurantsByLike() {
        return restaurantRepository.findTop3ByOrderByRestaurantLikeDesc();
    }

    // 이름 검색 투표 수 정렬
    @Transactional(readOnly = true)
    public List<Restaurant> findByRestaurantNameOrderByVote(String restaurantName) {
        return restaurantRepository.findByRestaurantNameOrderByVote(restaurantName);
    }

    // 이름 검색 좋아요 수 정렬
    @Transactional(readOnly = true)
    public List<Restaurant> findByRestaurantNameOrderByLike(String restaurantName) {
        return restaurantRepository.findByRestaurantNameOrderByLike(restaurantName);
    }
}
