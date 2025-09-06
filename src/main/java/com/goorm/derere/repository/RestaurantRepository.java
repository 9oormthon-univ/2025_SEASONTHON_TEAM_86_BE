package com.goorm.derere.repository;

import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.entity.RestaurantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // 소유자(userid)가 일치하는 행만 삭제하고 삭제 건수 반환
    long deleteByRestaurantIdAndUser_Userid(Long restaurantId, Long userid);

    Optional<Restaurant> findByRestaurantIdAndUser_Userid(Long restaurantId, Long userid);

    // 사용자별 음식점 조회 (OneToOne 관계이므로 하나만 존재)
    Optional<Restaurant> findByUser_Userid(Long userid);

    // 지역별 음식점 조회 (기본 정렬)
    List<Restaurant> findByRestaurantLocation(String location);

    // 지역별 음식점 조회 - 좋아요 내림차순 정렬
    List<Restaurant> findByRestaurantLocationOrderByRestaurantLikeDesc(String location);

    // 지역별 음식점 조회 - 투표수 내림차순 정렬
    List<Restaurant> findByRestaurantLocationOrderByRestaurantVoteDesc(String location);

    // 지역별 음식점 조회 - 투표수 오름차순 정렬
    List<Restaurant> findByRestaurantLocationOrderByRestaurantVoteAsc(String location);

    // 지역별 좋아요 TOP 1 음식점
    Optional<Restaurant> findTop1ByRestaurantLocationOrderByRestaurantLikeDesc(String location);

    // 지역별 좋아요 TOP 3 음식점
    List<Restaurant> findTop3ByRestaurantLocationOrderByRestaurantLikeDesc(String location);

    // 지역별 투표수 TOP 3 음식점
    List<Restaurant> findTop3ByRestaurantLocationOrderByRestaurantVoteDesc(String location);

    // 지역별 이름 검색 - 투표수 정렬
    @Query(value = "SELECT * FROM restaurant WHERE restaurant_location = :location AND MATCH(restaurant_name) AGAINST(:restaurantName) ORDER BY restaurant_vote DESC", nativeQuery = true)
    List<Restaurant> findByRestaurantLocationAndNameOrderByVote(@Param("location") String location, @Param("restaurantName") String restaurantName);

    // 지역별 이름 검색 - 좋아요수 정렬
    @Query(value = "SELECT * FROM restaurant WHERE restaurant_location = :location AND MATCH(restaurant_name) AGAINST(:restaurantName) ORDER BY restaurant_like DESC", nativeQuery = true)
    List<Restaurant> findByRestaurantLocationAndNameOrderByLike(@Param("location") String location, @Param("restaurantName") String restaurantName);

    // 지역별 타입 검색 - 투표수 내림차순 정렬
    List<Restaurant> findByRestaurantLocationAndRestaurantType_TypeNameOrderByRestaurantVoteDesc(String location, RestaurantType.TypeName typeName);

    // 지역별 타입 검색 - 투표수 오름차순 정렬
    List<Restaurant> findByRestaurantLocationAndRestaurantType_TypeNameOrderByRestaurantVoteAsc(String location, RestaurantType.TypeName typeName);

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

    @Query("SELECT r.restaurantVote FROM Restaurant r WHERE r.restaurantId = :restaurantId")
    Integer findVoteCountByRestaurantId(@Param("restaurantId") Long restaurantId);
}