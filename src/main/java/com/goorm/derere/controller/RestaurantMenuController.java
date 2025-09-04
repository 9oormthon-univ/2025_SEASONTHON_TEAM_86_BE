package com.goorm.derere.controller;

import com.goorm.derere.dto.AddRestaurantMenuRequest;
import com.goorm.derere.dto.RestaurantMenuResponse;
import com.goorm.derere.dto.UpdateRestaurantMenuRequest;
import com.goorm.derere.entity.User;
import com.goorm.derere.repository.OAuthRepository;
import com.goorm.derere.service.RestaurantMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "음식점 메뉴 API", description = "음식점 메뉴 CRUD 기능입니다.")
@RestController
@RequestMapping("/api/restaurant/menu")
@RequiredArgsConstructor
@Slf4j
public class RestaurantMenuController {

    private final RestaurantMenuService restaurantMenuService;
    private final OAuthRepository oAuthRepository;

    // 메뉴 생성 (강화된 검증 로직 포함)
    @Operation(summary = "메뉴 생성 API",
            description = "음식점 메뉴 CREATE 기능입니다. 로그인한 사용자 본인 소유의 음식점에만 메뉴를 추가할 수 있습니다. 같은 음식점 내에서 중복된 메뉴명은 등록할 수 없습니다.")
    @PostMapping
    public ResponseEntity<RestaurantMenuResponse> addRestaurantMenu(
            @RequestBody @Valid AddRestaurantMenuRequest request,
            @AuthenticationPrincipal DefaultOAuth2User oauthUser) {

        // OAuth2 인증 확인
        if (oauthUser == null) {
            log.warn("메뉴 생성 요청 - 인증되지 않은 사용자");
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        String email = (String) oauthUser.getAttribute("email");
        User user = oAuthRepository.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("메뉴 생성 요청 - 사용자 정보를 찾을 수 없음: {}", email);
                    return new IllegalStateException("사용자 정보가 없습니다.");
                });

        log.info("메뉴 생성 요청 - 사용자ID: {}, 음식점ID: {}, 메뉴명: {}",
                user.getUserid(), request.getRestaurantId(), request.getMenuName());

        try {
            RestaurantMenuResponse result = restaurantMenuService.addRestaurantMenu(request, user.getUserid());
            log.info("메뉴 생성 성공 - 메뉴ID: {}, 사용자ID: {}", result.getRestaurantMenuId(), user.getUserid());

            return ResponseEntity
                    .created(URI.create("/api/restaurant/menu/" + result.getRestaurantMenuId()))
                    .body(result);

        } catch (IllegalArgumentException e) {
            log.warn("메뉴 생성 실패 - 사용자ID: {}, 음식점ID: {}, 사유: {}",
                    user.getUserid(), request.getRestaurantId(), e.getMessage());
            throw e;
        }
    }

    // 특정 음식점의 모든 메뉴 조회
    @Operation(summary = "음식점 메뉴 조회 API", description = "특정 음식점의 모든 메뉴를 조회합니다.")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<RestaurantMenuResponse>> getMenusByRestaurant(
            @PathVariable Long restaurantId) {

        log.info("음식점 메뉴 조회 요청 - 음식점ID: {}", restaurantId);
        List<RestaurantMenuResponse> result = restaurantMenuService.getMenusByRestaurant(restaurantId);
        log.info("음식점 메뉴 조회 완료 - 음식점ID: {}, 메뉴 수: {}", restaurantId, result.size());

        return ResponseEntity.ok(result);
    }

    // 특정 음식점의 메뉴 개수 조회
    @Operation(summary = "음식점 메뉴 개수 조회 API", description = "특정 음식점의 총 메뉴 개수를 조회합니다.")
    @GetMapping("/restaurant/{restaurantId}/count")
    public ResponseEntity<Long> getMenuCountByRestaurant(@PathVariable Long restaurantId) {

        Long count = restaurantMenuService.getMenuCountByRestaurant(restaurantId);
        return ResponseEntity.ok(count);
    }

    // 메뉴 수정 - OAuth2 사용자 인증
    @Operation(summary = "메뉴 수정 API",
            description = "메뉴 UPDATE 기능입니다. 수정하고 싶은 필드 값만 입력해도 가능합니다. 로그인한 사용자의 음식점 메뉴만 수정 가능합니다.")
    @PutMapping("/{menuId}")
    public ResponseEntity<RestaurantMenuResponse> updateRestaurantMenu(
            @PathVariable Long menuId,
            @AuthenticationPrincipal DefaultOAuth2User oauthUser,
            @RequestBody @Valid UpdateRestaurantMenuRequest request) {

        if (oauthUser == null) {
            log.warn("메뉴 수정 요청 - 인증되지 않은 사용자");
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        String email = (String) oauthUser.getAttribute("email");
        User user = oAuthRepository.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("메뉴 수정 요청 - 사용자 정보를 찾을 수 없음: {}", email);
                    return new IllegalStateException("사용자 정보가 없습니다.");
                });

        log.info("메뉴 수정 요청 - 사용자ID: {}, 메뉴ID: {}", user.getUserid(), menuId);

        try {
            RestaurantMenuResponse result = restaurantMenuService
                    .updateRestaurantMenu(menuId, user.getUserid(), request);
            log.info("메뉴 수정 성공 - 메뉴ID: {}, 사용자ID: {}", menuId, user.getUserid());

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.warn("메뉴 수정 실패 - 사용자ID: {}, 메뉴ID: {}, 사유: {}",
                    user.getUserid(), menuId, e.getMessage());
            throw e;
        }
    }

    // 메뉴 삭제 - OAuth2 사용자 인증
    @Operation(summary = "메뉴 삭제 API",
            description = "메뉴 DELETE 기능입니다. 로그인한 사용자의 음식점 메뉴만 삭제 가능합니다.")
    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteRestaurantMenu(
            @PathVariable Long menuId,
            @AuthenticationPrincipal DefaultOAuth2User oauthUser) {

        if (oauthUser == null) {
            log.warn("메뉴 삭제 요청 - 인증되지 않은 사용자");
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        String email = (String) oauthUser.getAttribute("email");
        User user = oAuthRepository.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("메뉴 삭제 요청 - 사용자 정보를 찾을 수 없음: {}", email);
                    return new IllegalStateException("사용자 정보가 없습니다.");
                });

        log.info("메뉴 삭제 요청 - 사용자ID: {}, 메뉴ID: {}", user.getUserid(), menuId);

        try {
            restaurantMenuService.deleteRestaurantMenu(menuId, user.getUserid());
            log.info("메뉴 삭제 성공 - 메뉴ID: {}, 사용자ID: {}", menuId, user.getUserid());

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            log.warn("메뉴 삭제 실패 - 사용자ID: {}, 메뉴ID: {}, 사유: {}",
                    user.getUserid(), menuId, e.getMessage());
            throw e;
        }
    }

    // 개발용 임시 API (OAuth2 인증 없이 테스트) - 강화된 검증 포함
    @Operation(summary = "메뉴 생성 API (개발용)",
            description = "개발용 임시 API입니다. OAuth2 인증 없이 테스트할 수 있습니다. 소유권 검증은 동일하게 적용됩니다.")
    @PostMapping("/temp")
    public ResponseEntity<RestaurantMenuResponse> addRestaurantMenuTemp(
            @RequestBody @Valid AddRestaurantMenuRequest request,
            @RequestParam Long userId) {

        log.info("메뉴 생성 임시 요청 - 사용자ID: {}, 음식점ID: {}", userId, request.getRestaurantId());

        try {
            RestaurantMenuResponse result = restaurantMenuService.addRestaurantMenu(request, userId);
            log.info("메뉴 생성 임시 성공 - 메뉴ID: {}", result.getRestaurantMenuId());

            return ResponseEntity
                    .created(URI.create("/api/restaurant/menu/" + result.getRestaurantMenuId()))
                    .body(result);

        } catch (IllegalArgumentException e) {
            log.warn("메뉴 생성 임시 실패 - 사용자ID: {}, 사유: {}", userId, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "메뉴 수정 API (개발용)", description = "개발용 임시 API입니다. OAuth2 인증 없이 테스트할 수 있습니다.")
    @PutMapping("/{menuId}/temp")
    public ResponseEntity<RestaurantMenuResponse> updateRestaurantMenuTemp(
            @PathVariable Long menuId,
            @RequestParam Long userId,
            @RequestBody @Valid UpdateRestaurantMenuRequest request) {

        RestaurantMenuResponse result = restaurantMenuService
                .updateRestaurantMenuTemp(menuId, userId, request);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "메뉴 삭제 API (개발용)", description = "개발용 임시 API입니다. OAuth2 인증 없이 테스트할 수 있습니다.")
    @DeleteMapping("/{menuId}/temp")
    public ResponseEntity<Void> deleteRestaurantMenuTemp(
            @PathVariable Long menuId,
            @RequestParam Long userId) {

        restaurantMenuService.deleteRestaurantMenuTemp(menuId, userId);
        return ResponseEntity.noContent().build();
    }
}