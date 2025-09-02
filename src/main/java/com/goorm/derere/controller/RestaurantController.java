package com.goorm.derere.controller;

import com.goorm.derere.dto.AddRestaurantRequest;
import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.repository.RestaurantRepository;
import com.goorm.derere.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "음식점 API", description = "음식점 CRUD 기능 및 검색 기능 입니다.")
@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantRepository restaurantRepository;

    // 음식점 생성
    // TODO: userID FK 추가 예정
    @Operation(summary = "음식점 생성 API", description = "음식점 CREATE 기능입니다.")
    @PostMapping
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody @Valid AddRestaurantRequest addRestaurantRequest) {

        var result = restaurantService.addRestaurant(addRestaurantRequest);
        return ResponseEntity
                .created(URI.create("/api/restaurants/" + result.getRestaurantId()))
                .body(result);
    }
}
