package com.goorm.derere.dto;

import com.goorm.derere.entity.RestaurantType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateRestaurantRequest {

    @Size(max = 50)
    private String restaurantName;

    private String restaurantInfo;

    private RestaurantType.TypeName restaurantType;

    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
    private String restaurantNum;

    @Size(max = 100)
    private String restaurantAddress;

    @Size(max = 50)
    private String restaurantTime;

    // 입력 값이 null 이면 기본 값으로 설정 -> 원하는 필드만 업데이트
}