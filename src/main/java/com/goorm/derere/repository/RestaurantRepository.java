package com.goorm.derere.repository;

import com.goorm.derere.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // 소유자(userId)가 일치하는 행만 삭제하고 삭제 건수 반환
    long deleteByRestaurantIdAndUserId(Long restaurantId, Long userId);

    Optional<Restaurant> findByRestaurantIdAndUserId(Long restaurantId, Long userId);

    // 좋아요 내림차순 정렬
    List<Restaurant> findAllByOrderByRestaurantLikeDesc();

    // 좋아요 TOP 1 음식점
    Optional<Restaurant> findTop1ByOrderByRestaurantLikeDesc();

    // 좋아요 TOP 3 음식점
    List<Restaurant> findTop3ByOrderByRestaurantLikeDesc();

    // 이름 검색 투표수 정렬
    @Query(value = "SELECT * FROM restaurant WHERE MATCH(restaurant_name) AGAINST(?1) ORDER BY restaurant_vote DESC", nativeQuery = true)
    List<Restaurant> findByRestaurantNameOrderByVote(String restaurantName);

    // 이름 검색 좋아요수 정렬
    @Query(value = "SELECT * FROM restaurant WHERE MATCH(restaurant_name) AGAINST(?1) ORDER BY restaurant_like DESC", nativeQuery = true)
    List<Restaurant> findByRestaurantNameOrderByLike(String restaurantName);
}
