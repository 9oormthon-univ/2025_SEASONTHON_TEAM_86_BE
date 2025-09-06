package com.goorm.derere.dto;

import com.goorm.derere.entity.RestaurantType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AddRestaurantRequest {

    @NotBlank
    @Size(max = 50)
    private String restaurantName;

    @NotNull
    private Long userId;

    @NotBlank
    private String restaurantInfo;

    @NotNull
    private RestaurantType.TypeName restaurantType;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "연락처는 (0)00-(0)000-0000 형식이어야 합니다.")
    private String restaurantNum;
    
    @NotBlank @Size(max = 100)
    private String restaurantLocation;

    @NotBlank @Size(max = 20)
    private String restaurantStartTime;

    @NotBlank @Size(max = 20)
    private String restaurantEndTime;

    @Size(max = 500, message = "이미지 URL은 500자를 초과할 수 없습니다.")
    private String restaurantImageUrl;
}