package com.goorm.derere.service;

import com.goorm.derere.dto.LikeRequest;
import com.goorm.derere.dto.LikeResponse;
import com.goorm.derere.entity.Like;
import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.entity.User;
import com.goorm.derere.repository.LikeRepository;
import com.goorm.derere.repository.RestaurantRepository;
import com.goorm.derere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    // 좋아요 토글 (생성/수정)
    @Transactional
    public LikeResponse toggleLike(Long restaurantId, LikeRequest request) {
        // 음식점 존재 확인
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 음식점이 없습니다."));

        // 사용자 존재 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));

        // 기존 좋아요 조회
        Optional<Like> existingLike = likeRepository.findByUser_UseridAndRestaurant_RestaurantId(
                request.getUserId(), restaurantId);

        Like like;
        String message;
        boolean isCountChanged = false;

        if (existingLike.isPresent()) {
            // 기존 좋아요가 있는 경우
            like = existingLike.get();
            Boolean currentStatus = like.getLiked();
            Boolean requestedStatus = request.getLiked();

            if (!currentStatus.equals(requestedStatus)) {
                // 상태가 다르면 토글
                like.toggleLike(requestedStatus);

                // 음식점 좋아요 카운트 업데이트
                if (requestedStatus) {
                    // false -> true: 좋아요 증가
                    restaurant.setRestaurantLike(restaurant.getRestaurantLike() + 1);
                    message = "좋아요를 추가했습니다.";
                } else {
                    // true -> false: 좋아요 감소
                    restaurant.setRestaurantLike(Math.max(0, restaurant.getRestaurantLike() - 1));
                    message = "좋아요를 취소했습니다.";
                }
                isCountChanged = true;

                log.info("좋아요 상태 변경 - 사용자ID: {}, 음식점ID: {}, {} -> {}",
                        request.getUserId(), restaurantId, currentStatus, requestedStatus);
            } else {
                // 상태가 같으면 idempotent 처리
                message = requestedStatus ? "이미 좋아요 상태입니다." : "이미 좋아요 취소 상태입니다.";
                log.info("좋아요 상태 동일 - 사용자ID: {}, 음식점ID: {}, 상태: {}",
                        request.getUserId(), restaurantId, currentStatus);
            }
        } else {
            // 기존 좋아요가 없는 경우
            if (request.getLiked()) {
                // 새로운 좋아요 생성
                like = new Like(user, restaurant, true);
                like = likeRepository.save(like);

                // 음식점 좋아요 카운트 증가
                restaurant.setRestaurantLike(restaurant.getRestaurantLike() + 1);
                message = "좋아요를 추가했습니다.";
                isCountChanged = true;

                log.info("새 좋아요 생성 - 사용자ID: {}, 음식점ID: {}",
                        request.getUserId(), restaurantId);
            } else {
                // liked=false 요청인데 기록이 없으면 no-op
                like = new Like(user, restaurant, false);
                like = likeRepository.save(like);
                message = "좋아요 취소 상태로 설정되었습니다.";

                log.info("좋아요 취소 상태로 생성 - 사용자ID: {}, 음식점ID: {}",
                        request.getUserId(), restaurantId);
            }
        }

        if (isCountChanged) {
            restaurantRepository.save(restaurant);
        }

        return new LikeResponse(
                like.getLikeId(),
                request.getUserId(),
                restaurantId,
                restaurant.getRestaurantName(),
                like.getLiked(),
                restaurant.getRestaurantLike(),
                message
        );
    }

    // 사용자가 좋아요한 음식점 목록 조회
    @Transactional(readOnly = true)
    public List<Restaurant> getLikedRestaurantsByUser(Long userId) {
        // 사용자 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));

        return likeRepository.findLikedRestaurantsByUserId(userId);
    }

    // 특정 음식점의 좋아요 수 조회
    @Transactional(readOnly = true)
    public Long getLikeCountByRestaurant(Long restaurantId) {
        // 음식점 존재 확인
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 음식점이 없습니다."));

        return likeRepository.countLikesByRestaurantId(restaurantId);
    }
}