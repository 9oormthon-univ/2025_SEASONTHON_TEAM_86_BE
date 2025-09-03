package com.goorm.derere.exception;

import com.goorm.derere.dto.OAuthUserInfo;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2Exception extends OAuth2AuthenticationException {
    private final OAuthUserInfo userProfile;

    public OAuth2Exception(String msg, OAuthUserInfo userProfile) {
        super(msg);
        this.userProfile = userProfile;
    }

    public OAuthUserInfo getUserProfile() {
        return userProfile;
    }
}
