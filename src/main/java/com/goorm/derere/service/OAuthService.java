package com.goorm.derere.service;

import com.goorm.derere.dto.OAuthAttributes;
import com.goorm.derere.dto.OAuthLoginResult;
import com.goorm.derere.dto.OAuthUserInfo;
import com.goorm.derere.entity.User;
import com.goorm.derere.repository.OAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuthRepository oAuthRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuthUserInfo userProfile = OAuthAttributes.extract(registrationId, attributes);

        // 🔹 로그인 성공 처리 (DB 저장 없이)
        String role = getUserTypeIfExists(userProfile);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        Map<String, Object> customAttribute = getCustomAttribute(registrationId, userNameAttributeName, attributes, userProfile);
        customAttribute.put("oauth2User", userProfile);

        return new DefaultOAuth2User(authorities, customAttribute, userNameAttributeName);
    }


    public Map<String, Object> getCustomAttribute(String registrationId,
                                                  String userNameAttributeName,
                                                  Map<String, Object> attributes,
                                                  OAuthUserInfo userProfile) {
        Map<String, Object> customAttribute = new HashMap<>();
        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("userid", userProfile.getUserid());
        customAttribute.put("name", userProfile.getUsername());
        customAttribute.put("email", userProfile.getEmail());


        log.info("Custom Attribute 생성 완료: {}", customAttribute);
        return customAttribute;
    }

    public String getUserTypeIfExists(OAuthUserInfo userProfile) {
        // 이메일로 사용자 조회
        return oAuthRepository.findUserByEmail(userProfile.getEmail())
                .map(existingUser -> {
                    log.info("기존 회원 로그인 성공: {}", existingUser.getEmail());
                    return existingUser.getUsertype();  // 기존 회원이면 usertype 반환
                })
                .orElseGet(() -> {
                    log.info("신규 회원 로그인: {}", userProfile.getEmail());
                    return "신규";  // 신규 회원이면 null 반환
                });
    }


}
