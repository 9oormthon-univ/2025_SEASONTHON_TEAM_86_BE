package com.goorm.derere.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Restaurant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantId;

    // TODO: FK 추가 예정
    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String restaurantName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String restaurantInfo;

    @Column(nullable = false, length = 20)
    private String restaurantType;

    @Column(nullable = false, length = 13) // 최대 길이 13 ("000-0000-0000")
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

    // 음식점 생성
    public Restaurant(String restaurantName, Long userId, String restaurantInfo, String restaurantType,
                      String restaurantNum, String restaurantAddress, String restaurantTime) {
        if (restaurantName == null || restaurantName.isBlank()) throw new IllegalArgumentException("음식점 이름이 필요합니다.");
        if (userId == null) throw new IllegalArgumentException("userID 가 필요합니다.");
        if (!restaurantNum.matches("^\\d{3}-\\d{3,4}-\\d{4}$")) throw new IllegalArgumentException("잘못된 연락처 형식: " + restaurantNum);
        this.restaurantName = restaurantName;
        this.userId = userId;
        this.restaurantInfo = restaurantInfo;
        this.restaurantType = restaurantType;
        this.restaurantNum = restaurantNum;
        this.restaurantAddress = restaurantAddress;
        this.restaurantTime = restaurantTime;
    }

    // 음식점 수정
    public void changeName(String name){ this.restaurantName = name; }
    public void changeInfo(String info){ this.restaurantInfo = info; }
    public void changeType(String type){ this.restaurantType = type; }
    public void changeNum(String num){ this.restaurantNum = num; }
    public void changeAddress(String addr){ this.restaurantAddress = addr; }
    public void changeTime(String time){ this.restaurantTime = time; }
}
