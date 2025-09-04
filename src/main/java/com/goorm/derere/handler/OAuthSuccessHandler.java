package com.goorm.derere.handler;


import com.goorm.derere.dto.OAuthLoginResult;
import com.goorm.derere.dto.OAuthUserInfo;
import com.goorm.derere.service.OAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuthService oAuthService; // userProfile 처리용
    private static final String REDIRECT_URI_EXISTING = "http://3.39.193.85:8080";
    private static final String REDIRECT_URI_NEW = "http://3.39.193.85:8080/signup";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        OAuthUserInfo userProfile = (OAuthUserInfo) principal.getAttributes().get("oauth2User");

        HttpSession session = request.getSession();
        OAuthLoginResult result = oAuthService.updateOrSaveUser(userProfile, session);

        String targetUrl = result.isNew() ? REDIRECT_URI_NEW : REDIRECT_URI_EXISTING;
        response.sendRedirect(targetUrl);
    }


}

