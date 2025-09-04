package com.goorm.derere.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RestaurantType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_name", nullable = false, length = 20)
    private TypeName typeName;

    public enum TypeName {
        양식("양식"),
        한식("한식"),
        일식("일식"),
        중식("중식"),
        분식("분식"),
        카페_디저트("카페-디저트"),
        패스트푸드("패스트푸드"),
        기타("기타");

        private final String displayName;

        TypeName(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public RestaurantType(TypeName typeName) {
        if (typeName == null) throw new IllegalArgumentException("음식점 타입이 필요합니다.");
        this.typeName = typeName;
    }
}