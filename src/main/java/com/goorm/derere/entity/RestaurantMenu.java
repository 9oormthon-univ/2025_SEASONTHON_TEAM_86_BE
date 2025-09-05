package com.goorm.derere.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RestaurantMenu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long RestaurantMenuId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnore // JSON 직렬화 시 순환참조 방지
    private Restaurant restaurant;

    @Column(nullable = false, length = 100)
    private String menuName;

    @Column(nullable = false)
    private Integer menuPrice;

    @Column(columnDefinition = "TEXT")
    private String menuInfo;

    // 메뉴 이미지 URL (S3 이미지 주소)
    @Column(length = 500)
    private String menuImageUrl;

    // 메뉴 생성자
    public RestaurantMenu(Restaurant restaurant, String menuName, Integer menuPrice, String menuInfo) {
        validateMenuData(restaurant, menuName, menuPrice);
        this.restaurant = restaurant;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.menuInfo = menuInfo;
    }

    // 이미지 URL이 포함된 생성자
    public RestaurantMenu(Restaurant restaurant, String menuName, Integer menuPrice, String menuInfo, String menuImageUrl) {
        this(restaurant, menuName, menuPrice, menuInfo);
        this.menuImageUrl = menuImageUrl;
    }

    // 메뉴 수정 메소드
    public void changeName(String menuName) {this.menuName = menuName;}
    public void changePrice(Integer menuPrice) {this.menuPrice = menuPrice;}
    public void changeInfo(String menuInfo) {this.menuInfo = menuInfo;}
    public void changeImageUrl(String menuImageUrl) {this.menuImageUrl = menuImageUrl;}

    // 유효성 검증 메소드
    private void validateMenuData(Restaurant restaurant, String menuName, Integer menuPrice) {
        if (restaurant == null) {throw new IllegalArgumentException("음식점 정보가 필요합니다.");}
        if (menuName == null || menuName.trim().isEmpty()) {throw new IllegalArgumentException("메뉴 이름은 필수입니다.");}
        if (menuPrice == null || menuPrice < 0) {throw new IllegalArgumentException("메뉴 가격은 0 이상이어야 합니다.");}
    }
}