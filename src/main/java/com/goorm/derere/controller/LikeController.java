package com.goorm.derere.controller;

import com.goorm.derere.dto.LikeRequest;
import com.goorm.derere.dto.LikeResponse;
import com.goorm.derere.dto.RestaurantResponse;
import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.entity.User;
import com.goorm.derere.repository.OAuthRepository;
import com.goorm.derere.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "음식점 좋아요 API", description = "음식점 좋아요 기능 관련 API입니다.")
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Slf4j
public class LikeController {

    private final LikeService likeService;
    private final OAuthRepository oAuthRepository;

    // 좋아요 토글 (OAuth2 인증)
    @Operation(summary = "음식점 좋아요 토글 API",
            description = "음식점에 대한 좋아요를 추가하거나 취소합니다. liked=true(좋아요), liked=false(취소)")
    @PostMapping("/{restaurantId}/likes")
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable Long restaurantId,
            @RequestBody @Valid LikeRequest request,
            @AuthenticationPrincipal DefaultOAuth2User oauthUser) {

        // OAuth2 인증 확인
        if (oauthUser == null) {
            log.warn("좋아요 요청 - 인증되지 않은 사용자");
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        String email = (String) oauthUser.getAttribute("email");
        User user = oAuthRepository.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("좋아요 요청 - 사용자 정보를 찾을 수 없음: {}", email);
                    return new IllegalStateException("사용자 정보가 없습니다.");
                });

        // 요청의 userId와 인증된 사용자 ID 일치 확인
        if (!user.getUserid().equals(request.getUserId())) {
            log.warn("좋아요 요청 - 권한 불일치: 인증사용자ID={}, 요청사용자ID={}",
                    user.getUserid(), request.getUserId());
            throw new IllegalArgumentException("본인의 좋아요만 변경할 수 있습니다.");
        }

        log.info("좋아요 토글 요청 - 사용자ID: {}, 음식점ID: {}, 상태: {}",
                user.getUserid(), restaurantId, request.getLiked());

        try {
            LikeResponse response = likeService.toggleLike(restaurantId, request);
            log.info("좋아요 토글 성공 - 좋아요ID: {}, 메시지: {}",
                    response.getLikeId(), response.getMessage());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("좋아요 토글 실패 - 사용자ID: {}, 음식점ID: {}, 사유: {}",
                    user.getUserid(), restaurantId, e.getMessage());
            throw e;
        }
    }

    // 사용자가 좋아요한 음식점 목록 조회 (OAuth2 인증)
    @Operation(summary = "좋아요한 음식점 목록 API",
            description = "로그인한 사용자가 좋아요한 모든 음식점 목록을 조회합니다.")
    @GetMapping("/likes/my-list")
    public ResponseEntity<List<RestaurantResponse>> getMyLikedRestaurants(
            @AuthenticationPrincipal DefaultOAuth2User oauthUser) {

        // OAuth2 인증 확인
        if (oauthUser == null) {
            log.warn("좋아요 목록 조회 - 인증되지 않은 사용자");
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        String email = (String) oauthUser.getAttribute("email");
        User user = oAuthRepository.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("좋아요 목록 조회 - 사용자 정보를 찾을 수 없음: {}", email);
                    return new IllegalStateException("사용자 정보가 없습니다.");
                });

        log.info("좋아요 음식점 목록 조회 요청 - 사용자ID: {}", user.getUserid());

        try {
            List<Restaurant> likedRestaurants = likeService.getLikedRestaurantsByUser(user.getUserid());
            List<RestaurantResponse> response = likedRestaurants.stream()
                    .map(RestaurantResponse::new)
                    .collect(Collectors.toList());

            log.info("좋아요 음식점 목록 조회 성공 - 사용자ID: {}, 개수: {}",
                    user.getUserid(), response.size());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("좋아요 음식점 목록 조회 실패 - 사용자ID: {}, 사유: {}",
                    user.getUserid(), e.getMessage());
            throw e;
        }
    }

    // 특정 음식점의 좋아요 수 조회 (인증 불필요)
    @Operation(summary = "음식점 좋아요 수 조회 API",
            description = "특정 음식점의 총 좋아요 수를 조회합니다.")
    @GetMapping("/{restaurantId}/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long restaurantId) {

        log.info("음식점 좋아요 수 조회 요청 - 음식점ID: {}", restaurantId);

        try {
            Long likeCount = likeService.getLikeCountByRestaurant(restaurantId);
            log.info("음식점 좋아요 수 조회 성공 - 음식점ID: {}, 좋아요 수: {}",
                    restaurantId, likeCount);

            return ResponseEntity.ok(likeCount);

        } catch (IllegalArgumentException e) {
            log.warn("음식점 좋아요 수 조회 실패 - 음식점ID: {}, 사유: {}",
                    restaurantId, e.getMessage());
            throw e;
        }
    }

    // 개발용 임시 API (OAuth2 인증 없이 테스트)
    @Operation(summary = "좋아요 토글 API (개발용)",
            description = "개발용 임시 API입니다. OAuth2 인증 없이 테스트할 수 있습니다.")
    @PostMapping("/{restaurantId}/likes/temp")
    public ResponseEntity<LikeResponse> toggleLikeTemp(
            @PathVariable Long restaurantId,
            @RequestBody @Valid LikeRequest request) {

        log.info("좋아요 토글 임시 요청 - 사용자ID: {}, 음식점ID: {}, 상태: {}",
                request.getUserId(), restaurantId, request.getLiked());

        try {
            LikeResponse response = likeService.toggleLike(restaurantId, request);
            log.info("좋아요 토글 임시 성공 - 좋아요ID: {}", response.getLikeId());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("좋아요 토글 임시 실패 - 사용자ID: {}, 음식점ID: {}, 사유: {}",
                    request.getUserId(), restaurantId, e.getMessage());
            throw e;
        }
    }

    // 사용자가 좋아요한 음식점 목록 조회 (개발용)
    @Operation(summary = "좋아요한 음식점 목록 API (개발용)",
            description = "개발용 임시 API입니다. OAuth2 인증 없이 테스트할 수 있습니다.")
    @GetMapping("/likes/my-list/temp")
    public ResponseEntity<List<RestaurantResponse>> getMyLikedRestaurantsTemp(
            @RequestParam Long userId) {

        log.info("좋아요 음식점 목록 조회 임시 요청 - 사용자ID: {}", userId);

        try {
            List<Restaurant> likedRestaurants = likeService.getLikedRestaurantsByUser(userId);
            List<RestaurantResponse> response = likedRestaurants.stream()
                    .map(RestaurantResponse::new)
                    .collect(Collectors.toList());

            log.info("좋아요 음식점 목록 조회 임시 성공 - 사용자ID: {}, 개수: {}",
                    userId, response.size());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("좋아요 음식점 목록 조회 임시 실패 - 사용자ID: {}, 사유: {}",
                    userId, e.getMessage());
            throw e;
        }
    }
}