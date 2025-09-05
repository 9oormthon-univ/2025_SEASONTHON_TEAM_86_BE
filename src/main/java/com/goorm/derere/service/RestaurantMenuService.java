package com.goorm.derere.service;

import com.goorm.derere.dto.AddRestaurantMenuRequest;
import com.goorm.derere.dto.RestaurantMenuResponse;
import com.goorm.derere.dto.UpdateRestaurantMenuRequest;
import com.goorm.derere.entity.Restaurant;
import com.goorm.derere.entity.RestaurantMenu;
import com.goorm.derere.repository.RestaurantMenuRepository;
import com.goorm.derere.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantMenuService {

    private final RestaurantMenuRepository restaurantMenuRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Service s3Service;

    // 메뉴 생성
    @Transactional
    public RestaurantMenuResponse addRestaurantMenu(AddRestaurantMenuRequest request, Long userId) {
        // 음식점 존재 및 소유권 확인
        Restaurant restaurant = restaurantRepository.findByRestaurantIdAndUser_Userid(
                        request.getRestaurantId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 음식점이 없거나 접근 권한이 없습니다."));

        // 메뉴 생성 (이미지 URL 포함)
        RestaurantMenu menu;
        if (request.getMenuImageUrl() != null && !request.getMenuImageUrl().trim().isEmpty()) {
            menu = new RestaurantMenu(
                    restaurant,
                    request.getMenuName(),
                    request.getMenuPrice(),
                    request.getMenuInfo(),
                    request.getMenuImageUrl()
            );
        } else {
            menu = new RestaurantMenu(
                    restaurant,
                    request.getMenuName(),
                    request.getMenuPrice(),
                    request.getMenuInfo()
            );
        }

        RestaurantMenu savedMenu = restaurantMenuRepository.save(menu);
        log.info("메뉴 생성 완료 - ID: {}, 이름: {}, 이미지: {}",
                savedMenu.getRestaurantMenuId(),
                savedMenu.getMenuName(),
                savedMenu.getMenuImageUrl());

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

        // 기존 이미지 URL 백업 (이미지 교체 시 삭제용)
        String oldImageUrl = menu.getMenuImageUrl();

        // null이 아닌 값만 반영 (부분 수정)
        if (request.getMenuName() != null) {menu.changeName(request.getMenuName());}
        if (request.getMenuPrice() != null) {menu.changePrice(request.getMenuPrice());}
        if (request.getMenuInfo() != null) {menu.changeInfo(request.getMenuInfo());}

        // 이미지 URL 업데이트 처리
        if (request.getMenuImageUrl() != null) {
            menu.changeImageUrl(request.getMenuImageUrl());

            // 기존 이미지가 있고 새 이미지와 다르면 기존 이미지 삭제
            if (oldImageUrl != null && !oldImageUrl.equals(request.getMenuImageUrl())) {
                try {
                    s3Service.deleteImage(oldImageUrl);
                    log.info("메뉴 이미지 교체로 인한 기존 이미지 삭제 완료 - 메뉴ID: {}", menuId);
                } catch (Exception e) {
                    log.warn("기존 이미지 삭제 실패 - 메뉴ID: {}, 사유: {}", menuId, e.getMessage());
                }
            }
        }

        log.info("메뉴 수정 완료 - ID: {}, 이미지 변경: {}",
                menuId,
                request.getMenuImageUrl() != null);

        return new RestaurantMenuResponse(menu);
    }

    // 메뉴 삭제
    @Transactional
    public void deleteRestaurantMenu(Long menuId, Long userId) {
        // 삭제 전 이미지 URL 조회
        RestaurantMenu menu = restaurantMenuRepository.findByIdAndRestaurantOwner(menuId, userId)
                .orElse(null);

        int deletedCount = restaurantMenuRepository.deleteByIdAndRestaurantOwner(menuId, userId);
        if (deletedCount == 0) {throw new IllegalArgumentException("해당 메뉴가 없거나 삭제 권한이 없습니다.");}

        // 메뉴 삭제 후 S3 이미지도 삭제
        if (menu != null && menu.getMenuImageUrl() != null) {
            try {
                s3Service.deleteImage(menu.getMenuImageUrl());
                log.info("메뉴 삭제와 함께 이미지 삭제 완료 - 메뉴ID: {}", menuId);
            } catch (Exception e) {
                log.warn("메뉴 삭제는 완료되었으나 이미지 삭제 실패 - 메뉴ID: {}, 사유: {}",
                        menuId, e.getMessage());
            }
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