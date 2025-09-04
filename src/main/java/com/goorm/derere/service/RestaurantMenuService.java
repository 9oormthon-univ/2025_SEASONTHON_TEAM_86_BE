package com.goorm.derere.service;

import com.goorm.derere.dto.AddRestaurantMenuRequest;
import com.goorm.derere.dto.RestaurantMenuResponse;
import com.goorm.derere.dto.UpdateRestaurantMenuRequest;
import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.entity.RestaurantMenu;
import com.goorm.derere.repository.RestaurantMenuRepository;
import com.goorm.derere.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantMenuService {

    private final RestaurantMenuRepository restaurantMenuRepository;
    private final RestaurantRepository restaurantRepository;

    // 메뉴 생성
    @Transactional
    public RestaurantMenuResponse addRestaurantMenu(AddRestaurantMenuRequest request, Long userId) {
        // 음식점 존재 및 소유권 확인
        Restaurant restaurant = restaurantRepository.findByRestaurantIdAndUser_Userid(
                        request.getRestaurantId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 음식점이 없거나 접근 권한이 없습니다."));

        // 메뉴 생성
        RestaurantMenu menu = new RestaurantMenu(
                restaurant,
                request.getMenuName(),
                request.getMenuPrice(),
                request.getMenuInfo()
        );

        RestaurantMenu savedMenu = restaurantMenuRepository.save(menu);
        return new RestaurantMenuResponse(savedMenu);
    }

    // 특정 음식점의 모든 메뉴 조회
    @Transactional(readOnly = true)
    public List<RestaurantMenuResponse> getMenusByRestaurant(Long restaurantId) {
        // 음식점 존재 확인
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 음식점이 없습니다."));

        List<RestaurantMenu> menus = restaurantMenuRepository.findByRestaurant_RestaurantId(restaurantId);
        return menus.stream()
                .map(RestaurantMenuResponse::new)
                .collect(Collectors.toList());
    }

    // 메뉴 수정
    @Transactional
    public RestaurantMenuResponse updateRestaurantMenu(Long menuId, Long userId,
                                                       UpdateRestaurantMenuRequest request) {
        // 메뉴 존재 및 소유권 확인
        RestaurantMenu menu = restaurantMenuRepository.findByIdAndRestaurantOwner(menuId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 없거나 수정 권한이 없습니다."));

        // null이 아닌 값만 반영 (부분 수정)
        if (request.getMenuName() != null) {
            menu.changeName(request.getMenuName());
        }
        if (request.getMenuPrice() != null) {
            menu.changePrice(request.getMenuPrice());
        }
        if (request.getMenuInfo() != null) {
            menu.changeInfo(request.getMenuInfo());
        }

        return new RestaurantMenuResponse(menu);
    }

    // 메뉴 삭제
    @Transactional
    public void deleteRestaurantMenu(Long menuId, Long userId) {
        int deletedCount = restaurantMenuRepository.deleteByIdAndRestaurantOwner(menuId, userId);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("해당 메뉴가 없거나 삭제 권한이 없습니다.");
        }
    }

    // 특정 음식점의 메뉴 개수 조회
    @Transactional(readOnly = true)
    public long getMenuCountByRestaurant(Long restaurantId) {
        // 음식점 존재 확인
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 음식점이 없습니다."));

        return restaurantMenuRepository.countByRestaurant_RestaurantId(restaurantId);
    }

    // 개발용 임시 메뉴 수정 API (OAuth2 인증 없이 테스트)
    @Transactional
    public RestaurantMenuResponse updateRestaurantMenuTemp(Long menuId, Long userId,
                                                           UpdateRestaurantMenuRequest request) {
        return updateRestaurantMenu(menuId, userId, request);
    }

    // 개발용 임시 메뉴 삭제 API (OAuth2 인증 없이 테스트)
    @Transactional
    public void deleteRestaurantMenuTemp(Long menuId, Long userId) {
        deleteRestaurantMenu(menuId, userId);
    }
}