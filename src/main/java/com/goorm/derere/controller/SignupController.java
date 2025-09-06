package com.goorm.derere.controller;

import com.goorm.derere.dto.OAuthUserInfo;
import com.goorm.derere.entity.User;
import com.goorm.derere.repository.OAuthRepository;
import com.goorm.derere.service.LoginRedirectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignupController {

    private final OAuthRepository oAuthRepository;
    private final LoginRedirectService loginRedirectService;

//    @PostMapping("/signup")
//    public User signup(@AuthenticationPrincipal OAuthUserInfo oauthUser,
//                       @RequestBody Map<String, String> body) {
//
//        if (oauthUser == null) {
//            throw new IllegalStateException("OAuth 정보가 없습니다.");
//        }
//
//        User newUser = User.builder()
//                .username(oauthUser.getUsername())      // OAuth 닉네임
//                .email(oauthUser.getEmail())            // OAuth 이메일
//                .location(body.get("location"))         // React에서 보낸 JSON
//                .usertype(body.get("usertype"))         // React에서 보낸 JSON
//                .build();
//
//        newUser = oAuthRepository.save(newUser);
//
////        OAuthLoginResult result = new OAuthLoginResult(newUser, false); // 신규지만 저장 완료 후는 기존 사용자 취급
////        Authentication auth = new CustomAuthenticationToken(result); // 커스텀 토큰 사용
////        SecurityContextHolder.getContext().setAuthentication(auth);
//        return  newUser;
//    }
    @PostMapping("/signup")
    public void signup(@AuthenticationPrincipal OAuthUserInfo oauthUser,
                       @RequestBody Map<String, String> body,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {

        User newUser = User.builder()
                .username(oauthUser.getUsername())
                .email(oauthUser.getEmail())
                .location(body.get("location"))
                .usertype(body.get("usertype"))
                .build();
        newUser = oAuthRepository.save(newUser);

        String targetUrl = loginRedirectService.handleLogin(newUser, request);
        response.sendRedirect(targetUrl);
    }

}
