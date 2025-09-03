package com.goorm.derere.controller;

import com.goorm.derere.dto.AddRestaurantRequest;
import com.goorm.derere.dto.UpdateRestaurantRequest;
import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.repository.RestaurantRepository;
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

    // 수정
    // TODO: userID FK 추가 예정
    @Operation(summary = "음식점 수정 API", description = "음식점 UPDATE 기능입니다. 수정하고 싶은 필드 값만 입력해도 가능합니다.")
    @PutMapping("/{restaurantId}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long restaurantId, @RequestParam Long userId, @RequestBody @Valid UpdateRestaurantRequest updateRestaurantRequest) {

        restaurantService.updateRestaurant(restaurantId, userId, updateRestaurantRequest);
        return ResponseEntity.noContent().build();
    }

    // 삭제
    // TODO: userID FK 추가 예정
    @Operation(summary = "음식점 삭제 API", description = "음식점 DELETE 기능입니다.")
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long restaurantId, @RequestParam Long userId) { // ex) DELETE /api/restaurants/10?userId=1

        restaurantService.deleteRestaurant(restaurantId, userId);
        return ResponseEntity.noContent().build();
    }
}
