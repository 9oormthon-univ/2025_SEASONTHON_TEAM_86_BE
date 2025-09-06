package com.goorm.derere.service;

import com.goorm.derere.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class LoginRedirectService {
    private static final String REDIRECT_URI_MAIN = "http://localhost:5173/";
    private static final String REDIRECT_URI_OWNER = "http://localhost:5173/ownerhomepage";
    public String handleLogin(User user, HttpServletRequest request) {

        // 1. SecurityContext 갱신
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        // 2. usertype 기반 redirect URL 결정
        if ("고객".equalsIgnoreCase(user.getUsertype())) {
            return REDIRECT_URI_MAIN;
        } else if ("사장님".equalsIgnoreCase(user.getUsertype())) {
            return REDIRECT_URI_OWNER;
        } else {
            return REDIRECT_URI_MAIN;
        }
    }
}

