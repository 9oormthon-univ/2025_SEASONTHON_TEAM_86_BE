package com.goorm.derere.dto;

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
    private Long userId; // TODO: FK

    @NotBlank
    private String restaurantInfo;

    @NotBlank @Size(max = 20)
    private String restaurantType;

    @NotBlank
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "연락처는 (0)00-(0)000-0000 형식이어야 합니다.")
    private String restaurantNum;

    @NotBlank @Size(max = 100)
    private String restaurantAddress;

    @NotBlank @Size(max = 50) // "09:00 ~ 21:00" 같은 문자열 규칙을 정하면 Pattern도 고려
    private String restaurantTime;
}

