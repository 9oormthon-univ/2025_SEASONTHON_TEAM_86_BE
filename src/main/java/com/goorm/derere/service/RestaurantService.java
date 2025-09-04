package com.goorm.derere.service;

import com.goorm.derere.dto.AddRestaurantRequest;
import com.goorm.derere.dto.UpdateRestaurantRequest;
import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.entity.RestaurantType;
import com.goorm.derere.entity.User;
import com.goorm.derere.repository.RestaurantRepository;
import com.goorm.derere.repository.RestaurantTypeRepository;
import com.goorm.derere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTypeRepository restaurantTypeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Restaurant addRestaurant(AddRestaurantRequest addRestaurantRequest) {

        // User 엔티티 조회
        User user = userRepository.findById(addRestaurantRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));

        // 사용자가 이미 음식점을 소유하고 있는지 확인
        if (restaurantRepository.findByUser_Userid(user.getUserid()).isPresent()) {
            throw new IllegalArgumentException("사용자는 하나의 음식점만 소유할 수 있습니다.");
        }

        // RestaurantType 조회 또는 생성
        RestaurantType restaurantType = restaurantTypeRepository
                .findByTypeName(addRestaurantRequest.getRestaurantType())
                .orElseGet(() -> {
                    RestaurantType newType = new RestaurantType(addRestaurantRequest.getRestaurantType());
                    return restaurantTypeRepository.save(newType);
                });

        var restaurant = new Restaurant(
                addRestaurantRequest.getRestaurantName(),
                user,
                addRestaurantRequest.getRestaurantInfo(),
                restaurantType,
                addRestaurantRequest.getRestaurantNum(),
                addRestaurantRequest.getRestaurantAddress(),
                addRestaurantRequest.getRestaurantTime()
        );

        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId, Long userId) {
        long result = restaurantRepository.deleteByRestaurantIdAndUser_Userid(restaurantId, userId);
        if (result == 0) throw new IllegalArgumentException("삭제 권한 혹은 해당 음식점이 없습니다.");
    }

    @Transactional
    public void updateRestaurant(Long restaurantId, Long userId, UpdateRestaurantRequest updateRestaurantRequest) {

        var restaurant = restaurantRepository.findByRestaurantIdAndUser_Userid(restaurantId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 음식점이 없습니다."));

        // null이 아닌 값만 반영 (부분 수정)
        if (updateRestaurantRequest.getRestaurantName() != null) {
            restaurant.changeName(updateRestaurantRequest.getRestaurantName());
        }
        if (updateRestaurantRequest.getRestaurantInfo() != null) {
            restaurant.changeInfo(updateRestaurantRequest.getRestaurantInfo());
        }
        if (updateRestaurantRequest.getRestaurantType() != null) {
            RestaurantType restaurantType = restaurantTypeRepository
                    .findByTypeName(updateRestaurantRequest.getRestaurantType())
                    .orElseGet(() -> {
                        RestaurantType newType = new RestaurantType(updateRestaurantRequest.getRestaurantType());
                        return restaurantTypeRepository.save(newType);
                    });
            restaurant.changeType(restaurantType);
        }
        if (updateRestaurantRequest.getRestaurantNum() != null) {
            restaurant.changeNum(updateRestaurantRequest.getRestaurantNum());
        }
        if (updateRestaurantRequest.getRestaurantAddress() != null) {
            restaurant.changeAddress(updateRestaurantRequest.getRestaurantAddress());
        }
        if (updateRestaurantRequest.getRestaurantTime() != null) {
            restaurant.changeTime(updateRestaurantRequest.getRestaurantTime());
        }
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

    // 음식점 타입으로 검색 투표 많은 순 정렬
    @Transactional(readOnly = true)
    public List<Restaurant> findByRestaurantTypeOrderByVoteDesc(RestaurantType.TypeName typeName) {
        return restaurantRepository.findByRestaurantType_TypeNameOrderByRestaurantVoteDesc(typeName);
    }

    // 음식점 타입으로 검색 투표 적은 순 정렬
    @Transactional(readOnly = true)
    public List<Restaurant> findByRestaurantTypeOrderByVoteAsc(RestaurantType.TypeName typeName) {
        return restaurantRepository.findByRestaurantType_TypeNameOrderByRestaurantVoteAsc(typeName);
    }
}