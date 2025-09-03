package com.goorm.derere.controller;

import com.goorm.derere.dto.AddRestaurantRequest;
import com.goorm.derere.dto.UpdateRestaurantRequest;
import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "음식점 API", description = "음식점 CRUD 기능 및 검색 기능 입니다.")
@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 생성
    // TODO: userID FK 추가 예정
    @Operation(summary = "음식점 생성 API", description = "음식점 CREATE 기능입니다.")
    @PostMapping
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody @Valid AddRestaurantRequest addRestaurantRequest) {

        var result = restaurantService.addRestaurant(addRestaurantRequest);
        return ResponseEntity
                .created(URI.create("/api/restaurants/" + result.getRestaurantId()))
                .body(result);
    }

    // 전체 조회
    @Operation(summary = "음식점 조회 API", description = "음식점 READ 기능입니다. 전체 음식점 리스트를 확인할 수 있습니다.")
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {

        var result = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(result);
    }

    // 좋아요 정렬
    @Operation(summary = "전체 좋아요수 정렬 API",
            description = "전체 음식점을 좋아요수 내림차순으로 정렬하여 조회합니다.")
    @GetMapping("/like/all")
    public ResponseEntity<List<Restaurant>> getAllRestaurantsOrderByLike() {

        var result = restaurantService.getAllRestaurantsOrderByLike();
        return ResponseEntity.ok(result);
    }

    // 좋아요 TOP 1 음식점
    @Operation(summary = "좋아요 TOP 1 음식점 API", description = "좋아요수가 가장 많은 음식점 1곳을 조회합니다.")
    @GetMapping("/like/top1")
    public ResponseEntity<Restaurant> getTop1RestaurantByLike() {

        var restaurant = restaurantService.getTop1RestaurantByLike();
        return ResponseEntity.ok(restaurant);
    }

    // 좋아요 TOP 3 음식점
    @Operation(summary = "좋아요 TOP 3 음식점 API", description = "좋아요수가 가장 많은 3곳의 음식점을 조회합니다.")
    @GetMapping("/like/top3")
    public ResponseEntity<List<Restaurant>> getTop3RestaurantsByLike() {

        var restaurants = restaurantService.getTop3RestaurantsByLike();
        return ResponseEntity.ok(restaurants);
    }

    // 이름 검색 투표/좋아요 수 정렬 ex)/api/restaurant/search?restaurantName=스시&sortBy=like
    @Operation(summary = "이름 검색 API", description = "이름을 입력하여 음식점 리스트를 조회할 수 있습니다. vote/like로 정렬을 선택할 수 있습니다. (입력 없으면 기본으로 투표 수 정렬)")
    @GetMapping("/search")
    public ResponseEntity<List<Restaurant>> getRestaurantsByName(@RequestParam String restaurantName, @RequestParam(defaultValue = "vote") String sortBy) {

        List<Restaurant> restaurants;
        if (sortBy.equals("like")) { restaurants = restaurantService.findByRestaurantNameOrderByLike(restaurantName); }
        else { restaurants = restaurantService.findByRestaurantNameOrderByVote(restaurantName);}
        return ResponseEntity.ok(restaurants);
    }

    // 수정
    // TODO: userID FK 추가 예정
    @Operation(summary = "음식점 수정 API", description = "음식점 UPDATE 기능입니다. 수정하고 싶은 필드 값만 입력해도 가능합니다.")
    @PutMapping("/{restaurantId}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long restaurantId, @RequestParam Long userId, @RequestBody @Valid UpdateRestaurantRequest updateRestaurantRequest) {

        restaurantService.updateRestaurant(restaurantId, userId, updateRestaurantRequest);
        return ResponseEntity.noContent().build();
    }

    // 삭제 ex)/api/restaurants/10?userId=1
    // TODO: userID FK 추가 예정
    @Operation(summary = "음식점 삭제 API", description = "음식점 DELETE 기능입니다.")
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long restaurantId, @RequestParam Long userId) {

        restaurantService.deleteRestaurant(restaurantId, userId);
        return ResponseEntity.noContent().build();
    }
}
