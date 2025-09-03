package com.goorm.derere.repository;

import com.goorm.derere.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // 소유자(userId)가 일치하는 행만 삭제하고 삭제 건수 반환
    long deleteByRestaurantIdAndUserId(Long restaurantId, Long userId);
}
