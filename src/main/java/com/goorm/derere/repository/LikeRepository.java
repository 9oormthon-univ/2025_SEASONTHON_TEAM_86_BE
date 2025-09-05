package com.goorm.derere.repository;

import com.goorm.derere.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // 사용자와 음식점으로 좋아요 조회
    Optional<Like> findByUser_UseridAndRestaurant_RestaurantId(Long userId, Long restaurantId);

    // 특정 사용자의 모든 좋아요 조회
    List<Like> findByUser_UseridAndLikedTrue(Long userId);

    // 특정 음식점의 좋아요 수 조회 (liked=true만)
    @Query("SELECT COUNT(l) FROM Like l WHERE l.restaurant.restaurantId = :restaurantId AND l.liked = true")
    Long countLikesByRestaurantId(@Param("restaurantId") Long restaurantId);

    // 특정 사용자가 특정 음식점을 좋아요했는지 확인
    @Query("SELECT l.liked FROM Like l WHERE l.user.userid = :userId AND l.restaurant.restaurantId = :restaurantId")
    Optional<Boolean> findLikeStatusByUserAndRestaurant(@Param("userId") Long userId, @Param("restaurantId") Long restaurantId);

    // 음식점별 좋아요 수 통계 (관리자용)
    @Query("SELECT l.restaurant.restaurantId, COUNT(l) FROM Like l WHERE l.liked = true GROUP BY l.restaurant.restaurantId")
    List<Object[]> getLikeCountsByRestaurant();

    // 사용자의 좋아요한 음식점 목록 조회
    @Query("SELECT l.restaurant FROM Like l WHERE l.user.userid = :userId AND l.liked = true")
    List<com.goorm.derere.entity.Restaurant> findLikedRestaurantsByUserId(@Param("userId") Long userId);
}