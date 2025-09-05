package com.goorm.derere.repository;

import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.entity.RestaurantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // 소유자(userid)가 일치하는 행만 삭제하고 삭제 건수 반환
    long deleteByRestaurantIdAndUser_Userid(Long restaurantId, Long userid);

    Optional<Restaurant> findByRestaurantIdAndUser_Userid(Long restaurantId, Long userid);

    // 사용자별 음식점 조회 (OneToOne 관계이므로 하나만 존재)
    Optional<Restaurant> findByUser_Userid(Long userid);

    // 좋아요 내림차순 정렬
    List<Restaurant> findAllByOrderByRestaurantLikeDesc();

    // 좋아요 TOP 1 음식점
    Optional<Restaurant> findTop1ByOrderByRestaurantLikeDesc();

    // 좋아요 TOP 3 음식점
    List<Restaurant> findTop3ByOrderByRestaurantLikeDesc();

    // 투표수 내림차순 정렬
    List<Restaurant> findAllByOrderByRestaurantVoteDesc();

    // 투표수 TOP 3 음식점
    List<Restaurant> findTop3ByOrderByRestaurantVoteDesc();

    // 이름 검색 투표수 정렬
    @Query(value = "SELECT * FROM restaurant WHERE MATCH(restaurant_name) AGAINST(?1) ORDER BY restaurant_vote DESC", nativeQuery = true)
    List<Restaurant> findByRestaurantNameOrderByVote(String restaurantName);

    // 이름 검색 좋아요수 정렬
    @Query(value = "SELECT * FROM restaurant WHERE MATCH(restaurant_name) AGAINST(?1) ORDER BY restaurant_like DESC", nativeQuery = true)
    List<Restaurant> findByRestaurantNameOrderByLike(String restaurantName);

    // 음식점 타입으로 검색 투표수 내림차순 정렬 (투표 많은 순)
    List<Restaurant> findByRestaurantType_TypeNameOrderByRestaurantVoteDesc(RestaurantType.TypeName typeName);

    // 음식점 타입으로 검색 투표수 오름차순 정렬 (투표 적은 순)
    List<Restaurant> findByRestaurantType_TypeNameOrderByRestaurantVoteAsc(RestaurantType.TypeName typeName);
}