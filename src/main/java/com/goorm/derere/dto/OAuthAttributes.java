package com.goorm.derere.dto;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {
    KAKAO("kakao", (attribute) -> {

        Map<String, Object> account = (Map)attribute.get("kakao_account");
        Map<String, String> profile = (Map)account.get("profile");

        OAuthUserInfo userProfile = new OAuthUserInfo();

        userProfile.setUsername(profile.get("nickname"));
        userProfile.setEmail((String)account.get("email"));


        return userProfile;
    });
    private final String registrationId;
    private final Function<Map<String, Object>, OAuthUserInfo> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, OAuthUserInfo> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static OAuthUserInfo extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(value -> registrationId.equals(value.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }
}
