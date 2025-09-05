package com.goorm.derere.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Restaurant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantId;

    // User 엔티티와 OneToOne 관계 설정 (사용자는 하나의 음식점만 소유)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 50)
    private String restaurantName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String restaurantInfo;

    // RestaurantType과 ManyToOne 관계
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_type_id", nullable = false)
    private RestaurantType restaurantType;

    @Column(nullable = false, length = 13) // 최대 길이 13 ("(0)00-(0)000-0000")
    private String restaurantNum;

    @Column(nullable = false, length = 100)
    private String restaurantAddress;

    @Column(nullable = false, length = 50)
    private String restaurantTime;

    @Column(nullable = false)
    private Integer restaurantVote = 0;

    @Column(nullable = false)
    private Integer restaurantLike = 0;

    @Column(nullable = false)
    private boolean restaurantOpen = false;

    // 음식점 이미지 URL (S3 이미지 주소)
    @Column(length = 500)
    private String restaurantImageUrl;

    // RestaurantMenu와 OneToMany 관계 설정
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("restaurant") // JSON 직렬화 시 순환참조 방지
    private List<RestaurantMenu> menus = new ArrayList<>();

    // 음식점 생성
    public Restaurant(String restaurantName, User user, String restaurantInfo,
                      RestaurantType restaurantType, String restaurantNum, String restaurantAddress, String restaurantTime) {
        if (restaurantName == null || restaurantName.isBlank()) throw new IllegalArgumentException("음식점 이름이 필요합니다.");
        if (user == null) throw new IllegalArgumentException("사용자 정보가 필요합니다.");
        if (restaurantType == null) throw new IllegalArgumentException("음식점 타입이 필요합니다.");
        if (!restaurantNum.matches("^\\d{2,3}-\\d{3,4}-\\d{4}$")) throw new IllegalArgumentException("잘못된 연락처 형식: " + restaurantNum);
        this.restaurantName = restaurantName;
        this.user = user;
        this.restaurantInfo = restaurantInfo;
        this.restaurantType = restaurantType;
        this.restaurantNum = restaurantNum;
        this.restaurantAddress = restaurantAddress;
        this.restaurantTime = restaurantTime;
    }

    // 이미지 URL이 포함된 생성자
    public Restaurant(String restaurantName, User user, String restaurantInfo,
                      RestaurantType restaurantType, String restaurantNum, String restaurantAddress,
                      String restaurantTime, String restaurantImageUrl) {
        this(restaurantName, user, restaurantInfo, restaurantType, restaurantNum, restaurantAddress, restaurantTime);
        this.restaurantImageUrl = restaurantImageUrl;
    }

    // 음식점 수정
    public void changeName(String name){ this.restaurantName = name; }
    public void changeInfo(String info){ this.restaurantInfo = info; }
    public void changeType(RestaurantType type){ this.restaurantType = type; }
    public void changeNum(String num){ this.restaurantNum = num; }
    public void changeAddress(String addr){ this.restaurantAddress = addr; }
    public void changeTime(String time){ this.restaurantTime = time; }
    public void changeImageUrl(String imageUrl){ this.restaurantImageUrl = imageUrl; }

    // 메뉴 관련 편의 메소드
    public void addMenu(RestaurantMenu menu) {
        menus.add(menu);
    }

    public void removeMenu(RestaurantMenu menu) {
        menus.remove(menu);
    }

    // 메뉴 개수 조회
    public int getMenuCount() {
        return menus.size();
    }
}