package com.goorm.derere.dto;

import com.goorm.derere.entity.User;

public class OAuthLoginResult {
    private final User user;
    private final boolean isNew;

    public OAuthLoginResult(User user, boolean isNew) {
        this.user = user;
        this.isNew = isNew;
    }

    public User getUser() { return user; }
    public boolean isNew() { return isNew; }
}
