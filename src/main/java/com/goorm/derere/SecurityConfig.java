package com.goorm.derere;

import com.goorm.derere.entity.User;
import com.goorm.derere.handler.OAuthSuccessHandler;
import com.goorm.derere.service.OAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final OAuthService oAuthService;
    private final OAuthSuccessHandler oAuthSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        기존 코드
//        http.csrf((csrf) -> csrf.disable());
//
//        http.authorizeHttpRequests((authorize) ->
//                authorize.requestMatchers("/**").permitAll() // 개발 단계에서 임시로 모든 URL 허용
//        );

        //기존 코드에 소셜로그인에 필요한 코드 추가
        http.csrf((csrf) -> csrf.disable())
        .headers(headers -> headers.frameOptions(frame -> frame.disable())) // h2-console 허용
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/auth/**").permitAll()
                .anyRequest().authenticated() // 그 외 요청은 로그인 필요
        )
        .logout(logout -> logout
                .logoutSuccessUrl("/") // 로그아웃 후 이동
        )
            .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo.userService(oAuthService))
                    .successHandler(oAuthSuccessHandler)

            );
            return http.build();
    }
}
