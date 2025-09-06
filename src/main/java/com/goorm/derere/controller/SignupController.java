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

    @PostMapping("/signup")
    public void signup(@RequestBody Map<String, String> body,
                       @RequestParam("signupToken") String token,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(token) == null) {
            throw new IllegalStateException("세션에 OAuth 정보가 없습니다.");
        }

        OAuthUserInfo oauthUser = (OAuthUserInfo) session.getAttribute(token);

        User newUser = User.builder()
                .username(oauthUser.getUsername())
                .email(oauthUser.getEmail())
                .location(body.get("location"))
                .usertype(body.get("usertype"))
                .build();
        newUser = oAuthRepository.save(newUser);

        // 세션에 새 유저 저장 후 임시 토큰 제거
        session.setAttribute("user", newUser);
        session.removeAttribute(token);

        String targetUrl = loginRedirectService.handleLogin(newUser, request);
        response.sendRedirect(targetUrl);
    }
}

