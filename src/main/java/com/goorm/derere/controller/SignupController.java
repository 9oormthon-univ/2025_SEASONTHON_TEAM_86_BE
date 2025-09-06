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
public class SignupController {

    private final OAuthRepository oAuthRepository;

    @PostMapping("/signup")
    public User signup(@RequestBody Map<String, String> body) {


        User newUser = User.builder()
                .username(body.get("username"))      // OAuth 닉네임
                .email(body.get("email"))            // OAuth 이메일
                .location(body.get("location"))         // React에서 보낸 JSON
                .usertype(body.get("usertype"))         // React에서 보낸 JSON
                .build();

        return oAuthRepository.save(newUser); // DB 저장 후 JSON으로 반환
    }
}
