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

        // DB 저장 및 기존/신규 회원 판단
        OAuthLoginResult loginResult = updateOrSaveUser(userProfile);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

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

    public OAuthLoginResult updateOrSaveUser(OAuthUserInfo userProfile) {
        return oAuthRepository.findUserByEmail(userProfile.getEmail())
                .map(existingUser -> {
                    // 기존 회원 업데이트
                    existingUser.update(userProfile.getUsername(), userProfile.getEmail());
                    return new OAuthLoginResult(existingUser, false);
                })
                .orElseGet(() -> {
                    // 신규 회원 바로 DB에 저장
                    User newUser = User.builder()
                            .username(userProfile.getUsername())
                            .email(userProfile.getEmail())
                            .build();
                    User savedUser = oAuthRepository.save(newUser);
                    return new OAuthLoginResult(savedUser, true);
                });
    }
}
