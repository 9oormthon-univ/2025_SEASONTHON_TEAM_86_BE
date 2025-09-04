package com.goorm.derere.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateRestaurantMenuRequest {

    @Size(max = 100, message = "메뉴 이름은 100자를 초과할 수 없습니다.")
    private String menuName;

    @Min(value = 0, message = "메뉴 가격은 0 이상이어야 합니다.")
    private Integer menuPrice;

    @Size(max = 1000, message = "메뉴 설명은 1000자를 초과할 수 없습니다.")
    private String menuInfo;

    // 입력 값이 null이면 기본 값으로 설정 -> 원하는 필드만 업데이트
}