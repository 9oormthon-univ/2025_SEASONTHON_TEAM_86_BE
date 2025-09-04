package com.goorm.derere.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class AddRestaurantMenuRequest {

    @NotNull(message = "음식점 ID는 필수입니다.")
    @Positive(message = "음식점 ID는 양수여야 합니다.")
    private Long restaurantId;

    @NotBlank(message = "메뉴 이름은 필수입니다.")
    @Size(min = 1, max = 100, message = "메뉴 이름은 1자 이상 100자 이하여야 합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s\\-_()&.,!]+$", message = "메뉴 이름에 허용되지 않은 특수문자가 포함되어 있습니다.")
    private String menuName;

    @NotNull(message = "메뉴 가격은 필수입니다.")
    @Min(value = 0, message = "메뉴 가격은 0 이상이어야 합니다.")
    @Max(value = 1000000, message = "메뉴 가격은 1,000,000원을 초과할 수 없습니다.")
    private Integer menuPrice;

    @Size(max = 1000, message = "메뉴 설명은 1000자를 초과할 수 없습니다.")
    private String menuInfo;
}