package com.goorm.derere.controller;

import com.goorm.derere.dto.OAuthUserInfo;
import com.goorm.derere.entity.User;
import com.goorm.derere.repository.OAuthRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(
        origins = "http://localhost:5173", // 정확히 프론트 주소 명시
        allowCredentials = "true"
)
public class SignupController {

    private final OAuthRepository oAuthRepository;

    @PostMapping("/signup")
    public User signup(@AuthenticationPrincipal OAuthUserInfo oauthUser,
                       @RequestBody Map<String, String> body) {

        if (oauthUser == null) {
            throw new IllegalStateException("OAuth 정보가 없습니다.");
        }

        User newUser = User.builder()
                .username(oauthUser.getUsername())      // OAuth 닉네임
                .email(oauthUser.getEmail())            // OAuth 이메일
                .location(body.get("location"))         // React에서 보낸 JSON
                .usertype(body.get("usertype"))         // React에서 보낸 JSON
                .build();

        return oAuthRepository.save(newUser); // DB 저장 후 JSON으로 반환
    }
}
