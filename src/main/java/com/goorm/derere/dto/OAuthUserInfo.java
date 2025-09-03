package com.goorm.derere.dto;

import com.goorm.derere.entity.User;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthUserInfo {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userid;
    private String username;
    private String email;

    public User toEntity() {
        return User.builder()
                .userid(this.userid)
                .username(this.username)
                .email(this.email)
                .build();
    }
}
