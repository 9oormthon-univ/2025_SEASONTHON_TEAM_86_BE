package com.goorm.derere.service;

import com.goorm.derere.dto.AddRestaurantRequest;
import com.goorm.derere.dto.RestaurantDetailResponse;
import com.goorm.derere.dto.RestaurantResponse;
import com.goorm.derere.dto.UpdateRestaurantRequest;
import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.entity.RestaurantType;
import com.goorm.derere.entity.User;
import com.goorm.derere.repository.RestaurantRepository;
import com.goorm.derere.repository.RestaurantTypeRepository;
import com.goorm.derere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTypeRepository restaurantTypeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public RestaurantResponse addRestaurant(AddRestaurantRequest addRestaurantRequest) {

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

        // 이미지 URL이 있는 경우와 없는 경우 처리
        Restaurant restaurant;
        if (addRestaurantRequest.getRestaurantImageUrl() != null &&
                !addRestaurantRequest.getRestaurantImageUrl().trim().isEmpty()) {
            restaurant = new Restaurant(
                    addRestaurantRequest.getRestaurantName(),
                    user,
                    addRestaurantRequest.getRestaurantInfo(),
                    restaurantType,
                    addRestaurantRequest.getRestaurantNum(),
                    addRestaurantRequest.getRestaurantAddress(),
                    addRestaurantRequest.getRestaurantStartTime(),
                    addRestaurantRequest.getRestaurantEndTime(),
                    addRestaurantRequest.getRestaurantImageUrl()
            );
        } else {
            restaurant = new Restaurant(
                    addRestaurantRequest.getRestaurantName(),
                    user,
                    addRestaurantRequest.getRestaurantInfo(),
                    restaurantType,
                    addRestaurantRequest.getRestaurantNum(),
                    addRestaurantRequest.getRestaurantAddress(),
                    addRestaurantRequest.getRestaurantStartTime(),
                    addRestaurantRequest.getRestaurantEndTime()
            );
        }

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("음식점 생성 완료 - ID: {}, 이름: {}, 이미지: {}",
                savedRestaurant.getRestaurantId(),
                savedRestaurant.getRestaurantName(),
                savedRestaurant.getRestaurantImageUrl());

        return new RestaurantResponse(savedRestaurant);
    }

    @Transactional
    public void deleteRestaurant(Long restaurantId, Long userId) {
        // 삭제 전 이미지 URL 조회
        Restaurant restaurant = restaurantRepository.findByRestaurantIdAndUser_Userid(restaurantId, userId)
                .orElse(null);

        long result = restaurantRepository.deleteByRestaurantIdAndUser_Userid(restaurantId, userId);
        if (result == 0) {throw new IllegalArgumentException("삭제 권한 혹은 해당 음식점이 없습니다.");}

        // 음식점 삭제 후 S3 이미지도 삭제
        if (restaurant != null && restaurant.getRestaurantImageUrl() != null) {
            try {
                s3Service.deleteImage(restaurant.getRestaurantImageUrl());
                log.info("음식점 삭제와 함께 이미지 삭제 완료 - 음식점ID: {}", restaurantId);
            } catch (Exception e) {
                log.warn("음식점 삭제는 완료되었으나 이미지 삭제 실패 - 음식점ID: {}, 사유: {}",
                        restaurantId, e.getMessage());
            }
        }
    }

    @Transactional
    public void updateRestaurant(Long restaurantId, Long userId, UpdateRestaurantRequest updateRestaurantRequest) {

        var restaurant = restaurantRepository.findByRestaurantIdAndUser_Userid(restaurantId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 음식점이 없습니다."));

        // 기존 이미지 URL 백업 (이미지 교체 시 삭제용)
        String oldImageUrl = restaurant.getRestaurantImageUrl();

        // null이 아닌 값만 반영 (부분 수정)
        if (updateRestaurantRequest.getRestaurantName() != null) {restaurant.changeName(updateRestaurantRequest.getRestaurantName());}
        if (updateRestaurantRequest.getRestaurantInfo() != null) {restaurant.changeInfo(updateRestaurantRequest.getRestaurantInfo());}
        if (updateRestaurantRequest.getRestaurantType() != null) {
            RestaurantType restaurantType = restaurantTypeRepository
                    .findByTypeName(updateRestaurantRequest.getRestaurantType())
                    .orElseGet(() -> {
                        RestaurantType newType = new RestaurantType(updateRestaurantRequest.getRestaurantType());
                        return restaurantTypeRepository.save(newType);
                    });
            restaurant.changeType(restaurantType);
        }
        if (updateRestaurantRequest.getRestaurantNum() != null) {restaurant.changeNum(updateRestaurantRequest.getRestaurantNum());}
        if (updateRestaurantRequest.getRestaurantAddress() != null) {restaurant.changeAddress(updateRestaurantRequest.getRestaurantAddress());}
        if (updateRestaurantRequest.getRestaurantStartTime() != null) {restaurant.changeStartTime(updateRestaurantRequest.getRestaurantStartTime());}
        if (updateRestaurantRequest.getRestaurantEndTime() != null) {restaurant.changeEndTime(updateRestaurantRequest.getRestaurantEndTime());}

        // 이미지 URL 업데이트 처리
        if (updateRestaurantRequest.getRestaurantImageUrl() != null) {
            restaurant.changeImageUrl(updateRestaurantRequest.getRestaurantImageUrl());

            // 기존 이미지가 있고 새 이미지와 다르면 기존 이미지 삭제
            if (oldImageUrl != null && !oldImageUrl.equals(updateRestaurantRequest.getRestaurantImageUrl())) {
                try {
                    s3Service.deleteImage(oldImageUrl);
                    log.info("음식점 이미지 교체로 인한 기존 이미지 삭제 완료 - 음식점ID: {}", restaurantId);
                } catch (Exception e) {
                    log.warn("기존 이미지 삭제 실패 - 음식점ID: {}, 사유: {}", restaurantId, e.getMessage());
                }
            }
        }

        log.info("음식점 수정 완료 - ID: {}, 이미지 변경: {}",
                restaurantId,
                updateRestaurantRequest.getRestaurantImageUrl() != null);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(RestaurantResponse::new)
                .collect(Collectors.toList());
    }

    // 단일 음식점 조회 (메뉴 포함)
    @Transactional(readOnly = true)
    public RestaurantDetailResponse getRestaurantById(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 음식점이 없습니다."));
        return new RestaurantDetailResponse(restaurant);
    }

    // 좋아요 내림차순 정렬
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurantsOrderByLike() {
        return restaurantRepository.findAllByOrderByRestaurantLikeDesc().stream()
                .map(RestaurantResponse::new)
                .collect(Collectors.toList());
    }

    // 좋아요 TOP 1 음식점
    @Transactional(readOnly = true)
    public RestaurantResponse getTop1RestaurantByLike() {
        Restaurant restaurant = restaurantRepository.findTop1ByOrderByRestaurantLikeDesc()
                .orElseThrow(() -> new IllegalArgumentException("좋아요 TOP 1 음식점이 없습니다."));
        return new RestaurantResponse(restaurant);
    }

    // 좋아요 TOP 3 음식점
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getTop3RestaurantsByLike() {
        return restaurantRepository.findTop3ByOrderByRestaurantLikeDesc().stream()
                .map(RestaurantResponse::new)
                .collect(Collectors.toList());
    }

    // 투표수 내림차순 정렬
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurantsOrderByVote() {
        return restaurantRepository.findAllByOrderByRestaurantVoteDesc().stream()
                .map(RestaurantResponse::new)
                .collect(Collectors.toList());
    }

    // 투표수 TOP 3 음식점
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getTop3RestaurantsByVote() {
        return restaurantRepository.findTop3ByOrderByRestaurantVoteDesc().stream()
                .map(RestaurantResponse::new)
                .collect(Collectors.toList());
    }

    // 이름 검색 투표 수 정렬
    @Transactional(readOnly = true)
    public List<RestaurantResponse> findByRestaurantNameOrderByVote(String restaurantName) {
        return restaurantRepository.findByRestaurantNameOrderByVote(restaurantName).stream()
                .map(RestaurantResponse::new)
                .collect(Collectors.toList());
    }

    // 이름 검색 좋아요 수 정렬
    @Transactional(readOnly = true)
    public List<RestaurantResponse> findByRestaurantNameOrderByLike(String restaurantName) {
        return restaurantRepository.findByRestaurantNameOrderByLike(restaurantName).stream()
                .map(RestaurantResponse::new)
                .collect(Collectors.toList());
    }

    // 음식점 타입으로 검색 투표 많은 순 정렬
    @Transactional(readOnly = true)
    public List<RestaurantResponse> findByRestaurantTypeOrderByVoteDesc(RestaurantType.TypeName typeName) {
        return restaurantRepository.findByRestaurantType_TypeNameOrderByRestaurantVoteDesc(typeName).stream()
                .map(RestaurantResponse::new)
                .collect(Collectors.toList());
    }

    // 음식점 타입으로 검색 투표 적은 순 정렬
    @Transactional(readOnly = true)
    public List<RestaurantResponse> findByRestaurantTypeOrderByVoteAsc(RestaurantType.TypeName typeName) {
        return restaurantRepository.findByRestaurantType_TypeNameOrderByRestaurantVoteAsc(typeName).stream()
                .map(RestaurantResponse::new)
                .collect(Collectors.toList());
    }
}