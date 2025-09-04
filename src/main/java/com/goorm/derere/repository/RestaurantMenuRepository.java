package com.goorm.derere.repository;

import com.goorm.derere.entity.RestaurantMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantMenuRepository extends JpaRepository<RestaurantMenu, Long> {

    // 특정 음식점의 모든 메뉴 조회
    List<RestaurantMenu> findByRestaurant_RestaurantId(Long restaurantId);

    // 특정 음식점의 메뉴 개수 조회
    long countByRestaurant_RestaurantId(Long restaurantId);

    // 메뉴 ID와 음식점 소유자 ID로 메뉴 조회 (권한 확인용)
    @Query("SELECT m FROM RestaurantMenu m WHERE m.id = :menuId AND m.restaurant.user.userid = :userId")
    Optional<RestaurantMenu> findByIdAndRestaurantOwner(@Param("menuId") Long menuId, @Param("userId") Long userId);

    // 메뉴 ID와 음식점 소유자 ID로 메뉴 삭제 (권한 확인 후 삭제)
    @Modifying
    @Query("DELETE FROM RestaurantMenu m WHERE m.id = :menuId AND m.restaurant.user.userid = :userId")
    int deleteByIdAndRestaurantOwner(@Param("menuId") Long menuId, @Param("userId") Long userId);
}