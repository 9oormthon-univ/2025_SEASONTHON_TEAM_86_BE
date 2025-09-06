package com.goorm.derere.handler;


import com.goorm.derere.dto.OAuthLoginResult;
import com.goorm.derere.dto.OAuthUserInfo;
import com.goorm.derere.entity.User;
import com.goorm.derere.service.LoginRedirectService;
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
    private static final String REDIRECT_URI_NEW = "http://localhost:5173/signup";
    private final LoginRedirectService loginRedirectService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        OAuthUserInfo userProfile = (OAuthUserInfo) principal.getAttributes().get("oauth2User");

        HttpSession session = request.getSession();
        OAuthLoginResult result = oAuthService.updateOrSaveUser(userProfile, session);

        //추가되는 코드
        User user = result.getUser();
        String targetUrl;
        if (result.isNew()) {
            targetUrl = REDIRECT_URI_NEW;
        } else {
            targetUrl = loginRedirectService.handleLogin(user, request);
        }

        response.sendRedirect(targetUrl);

        //기존 코드
//        String targetUrl = result.isNew() ? REDIRECT_URI_NEW : REDIRECT_URI_EXISTING;
//        response.sendRedirect(targetUrl);
    }


}

