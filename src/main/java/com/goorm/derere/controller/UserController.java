package com.goorm.derere.controller;

import com.goorm.derere.entity.User;
import com.goorm.derere.repository.OAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final OAuthRepository oAuthRepository;

    @GetMapping("/")
    public User getCurrentUser(@AuthenticationPrincipal DefaultOAuth2User oauthUser) {
        if (oauthUser == null) {
            throw new IllegalStateException("로그인한 사용자가 없습니다.");
        }

        String email = (String) oauthUser.getAttribute("email");
        return oAuthRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("DB에 사용자 정보가 없습니다."));
    }

    @DeleteMapping("/")
    public String deleteUser(@AuthenticationPrincipal DefaultOAuth2User oauthUser) {
        if (oauthUser == null) {
            throw new IllegalStateException("로그인한 사용자가 없습니다.");
        }

        String email = (String) oauthUser.getAttribute("email");
        User user = oAuthRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("DB에 사용자 정보가 없습니다."));

        oAuthRepository.delete(user);

        return "회원 탈퇴 완료";
    }
}
