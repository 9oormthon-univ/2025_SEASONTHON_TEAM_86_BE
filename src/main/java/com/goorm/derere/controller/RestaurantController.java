package com.goorm.derere.controller;

import com.goorm.derere.dto.AddRestaurantRequest;
import com.goorm.derere.dto.RestaurantDetailResponse;
import com.goorm.derere.dto.RestaurantResponse;
import com.goorm.derere.dto.UpdateRestaurantRequest;
import com.goorm.derere.entity.RestaurantType;
import com.goorm.derere.entity.User;
import com.goorm.derere.repository.OAuthRepository;
import com.goorm.derere.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "음식점 API", description = "음식점 CRUD 기능 및 검색 기능입니다.")
@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final OAuthRepository oAuthRepository;

    // 생성
    @Operation(summary = "음식점 생성 API", description = "음식점 CREATE 기능입니다.")
    @PostMapping
    public ResponseEntity<RestaurantResponse> addRestaurant(@RequestBody @Valid AddRestaurantRequest addRestaurantRequest) {

        var result = restaurantService.addRestaurant(addRestaurantRequest);
        return ResponseEntity
                .created(URI.create("/api/restaurants/" + result.getRestaurantId()))
                .body(result);
    }

    // 전체 조회
    @Operation(summary = "음식점 조회 API", description = "음식점 READ 기능입니다. 전체 음식점 리스트를 확인할 수 있습니다.")
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {

        var result = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(result);
    }

    // 단일 음식점 조회 (메뉴 포함)
    @Operation(summary = "단일 음식점 상세 조회 API", description = "특정 음식점의 상세 정보와 메뉴 리스트를 함께 조회합니다.")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailResponse> getRestaurantById(@PathVariable Long restaurantId) {

        var result = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(result);
    }

    // 좋아요 정렬
    @Operation(summary = "전체 좋아요수 정렬 API",
            description = "전체 음식점을 좋아요수 내림차순으로 정렬하여 조회합니다.")
    @GetMapping("/like/all")
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurantsOrderByLike() {

        var result = restaurantService.getAllRestaurantsOrderByLike();
        return ResponseEntity.ok(result);
    }

    // 좋아요 TOP 1 음식점
    @Operation(summary = "좋아요 TOP 1 음식점 API", description = "좋아요수가 가장 많은 음식점 1곳을 조회합니다.")
    @GetMapping("/like/top1")
    public ResponseEntity<RestaurantResponse> getTop1RestaurantByLike() {

        var restaurant = restaurantService.getTop1RestaurantByLike();
        return ResponseEntity.ok(restaurant);
    }

    // 좋아요 TOP 3 음식점
    @Operation(summary = "좋아요 TOP 3 음식점 API", description = "좋아요수가 가장 많은 3곳의 음식점을 조회합니다.")
    @GetMapping("/like/top3")
    public ResponseEntity<List<RestaurantResponse>> getTop3RestaurantsByLike() {

        var restaurants = restaurantService.getTop3RestaurantsByLike();
        return ResponseEntity.ok(restaurants);
    }

    // 이름 검색 투표/좋아요 수 정렬 ex)/api/restaurant/search?restaurantName=스시&sortBy=like
    @Operation(summary = "이름 검색 API", description = "이름을 입력하여 음식점 리스트를 조회할 수 있습니다. vote/like로 정렬을 선택할 수 있습니다. (입력 없으면 기본으로 투표 수 정렬)")
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByName(@RequestParam String restaurantName, @RequestParam(defaultValue = "vote") String sortBy) {

        List<RestaurantResponse> restaurants;
        if (sortBy.equals("like")) {
            restaurants = restaurantService.findByRestaurantNameOrderByLike(restaurantName);
        } else {
            restaurants = restaurantService.findByRestaurantNameOrderByVote(restaurantName);
        }
        return ResponseEntity.ok(restaurants);
    }

    // 음식점 타입별 조회 ex)/api/restaurant/type/한식?sortBy=desc
    @Operation(summary = "음식점 타입별 조회 API",
            description = "음식점 타입(양식,한식,일식,중식,분식,카페-디저트,패스트푸드,기타)별 음식점 리스트를 조회할 수 있습니다. sortBy=desc(투표 많은 순, 기본값), sortBy=asc(투표 적은 순)")
    @GetMapping("/type/{restaurantType}")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByType(
            @PathVariable RestaurantType.TypeName restaurantType,
            @RequestParam(defaultValue = "desc") String sortBy) {

        List<RestaurantResponse> restaurants;
        if (sortBy.equals("asc")) {
            restaurants = restaurantService.findByRestaurantTypeOrderByVoteAsc(restaurantType);
        } else {
            restaurants = restaurantService.findByRestaurantTypeOrderByVoteDesc(restaurantType);
        }
        return ResponseEntity.ok(restaurants);
    }

    // 수정 - OAuth2 사용자 인증
    @Operation(summary = "음식점 수정 API", description = "음식점 UPDATE 기능입니다. 수정하고 싶은 필드 값만 입력해도 가능합니다. 로그인한 사용자의 음식점만 수정 가능합니다.")
    @PutMapping("/{restaurantId}")
    public ResponseEntity<Void> updateRestaurant(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal DefaultOAuth2User oauthUser,
            @RequestBody @Valid UpdateRestaurantRequest updateRestaurantRequest) {

        if (oauthUser == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        String email = (String) oauthUser.getAttribute("email");
        User user = oAuthRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));

        restaurantService.updateRestaurant(restaurantId, user.getUserid(), updateRestaurantRequest);
        return ResponseEntity.noContent().build();
    }

    // 삭제 - OAuth2 사용자 인증
    @Operation(summary = "음식점 삭제 API", description = "음식점 DELETE 기능입니다. 로그인한 사용자의 음식점만 삭제 가능합니다.")
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal DefaultOAuth2User oauthUser) {

        if (oauthUser == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        String email = (String) oauthUser.getAttribute("email");
        User user = oAuthRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));

        restaurantService.deleteRestaurant(restaurantId, user.getUserid());
        return ResponseEntity.noContent().build();
    }

    // 개발용 임시 API (OAuth2 인증 없이 테스트) http://localhost:8080/api/restaurant/1/temp?userId=1
    @Operation(summary = "음식점 수정 API (개발용)", description = "개발용 임시 API입니다. OAuth2 인증 없이 테스트할 수 있습니다.")
    @PutMapping("/{restaurantId}/temp")
    public ResponseEntity<Void> updateRestaurantTemp(
            @PathVariable Long restaurantId,
            @RequestParam Long userId,
            @RequestBody @Valid UpdateRestaurantRequest updateRestaurantRequest) {

        restaurantService.updateRestaurant(restaurantId, userId, updateRestaurantRequest);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "음식점 삭제 API (개발용)", description = "개발용 임시 API입니다. OAuth2 인증 없이 테스트할 수 있습니다.")
    @DeleteMapping("/{restaurantId}/temp")
    public ResponseEntity<Void> deleteRestaurantTemp(
            @PathVariable Long restaurantId,
            @RequestParam Long userId) {

        restaurantService.deleteRestaurant(restaurantId, userId);
        return ResponseEntity.noContent().build();
    }
}