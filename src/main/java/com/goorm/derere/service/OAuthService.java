package com.goorm.derere.service;

import com.goorm.derere.dto.OAuthAttributes;
import com.goorm.derere.dto.OAuthLoginResult;
import com.goorm.derere.dto.OAuthUserInfo;
import com.goorm.derere.entity.User;
import com.goorm.derere.repository.OAuthRepository;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

        // 1️⃣ 읽기 전용 Map을 수정 가능한 HashMap으로 변환
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        OAuthUserInfo userProfile = OAuthAttributes.extract(registrationId, attributes);

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();

        // DB에 이미 가입된 사용자 확인
        User user = oAuthRepository.findUserByEmail(userProfile.getEmail())
                .map(existingUser -> existingUser.update(userProfile.getUsername(), userProfile.getEmail()))
                .orElse(null);

        if (user == null) {
            // 신규 회원이면 임시 토큰 생성 후 세션에 저장
            String tempToken = UUID.randomUUID().toString();
            session.setAttribute(tempToken, userProfile);
            attributes.put("signupToken", tempToken); // 이제 오류 없음
            log.debug("세션에 oauth2User 저장 완료 - username: {}, email: {}, token: {}",
                    userProfile.getUsername(), userProfile.getEmail(), tempToken);
        }

        List<SimpleGrantedAuthority> authorities = user != null
                ? List.of(new SimpleGrantedAuthority("ROLE_USER"))
                : new ArrayList<>();

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

    public OAuthLoginResult updateOrSaveUser(OAuthUserInfo userProfile, HttpSession session) {
        Optional<User> optionalUser = oAuthRepository.findUserByEmail(userProfile.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get().update(userProfile.getUsername(), userProfile.getEmail());
            return new OAuthLoginResult(user, false); // 기존 회원
        } else {
            // 신규 회원이면 임시 토큰 생성 후 세션에 저장
            String tempToken = UUID.randomUUID().toString();
            session.setAttribute(tempToken, userProfile);
            log.debug("세션에 oauth2User 저장 완료 - username: {}, email: {}, token: {}",
                    userProfile.getUsername(), userProfile.getEmail(), tempToken);
            return new OAuthLoginResult(null, true);
        }
    }
}
