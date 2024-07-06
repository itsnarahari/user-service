package com.user.security.oauth2.user;

import com.user.dto.AuthProvider;
import com.user.entities.Role;

import java.util.Map;

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();

    public abstract Boolean getEmailVerified();
    public abstract String getUserName();
    public abstract String getFirstName();
    public abstract String getLastName();
    public abstract String getProvider();
    public abstract String getProviderId();
    public abstract AuthProvider getAuthProvider();
    public abstract Role getRole();
}
