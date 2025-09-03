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

    @NotBlank
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "연락처는 (0)00-(0)000-0000 형식이어야 합니다.")
    private String restaurantNum;

    @NotBlank @Size(max = 100)
    private String restaurantAddress;

    @NotBlank @Size(max = 50)
    private String restaurantTime;
}