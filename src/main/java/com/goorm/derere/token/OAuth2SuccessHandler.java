package com.goorm.derere.token;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler  implements AuthenticationSuccessHandler{

    private final TokenProvider tokenProvider;
    private static final String REDIRECT_URI = "http://localhost:5173/";

    @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws IOException, ServletException {
        // Access Token 발급
        String accessToken = tokenProvider.generateAccessToken(authentication);

        // Redirect 방식으로 토큰 전달 (QueryParam 방식)
        String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        response.sendRedirect(targetUrl);
    }
}
