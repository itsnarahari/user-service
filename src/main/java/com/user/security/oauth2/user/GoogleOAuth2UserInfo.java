package com.user.security.oauth2.user;

import com.user.dto.AuthProvider;
import com.user.entities.Role;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }

    @Override
    public Boolean getEmailVerified() {
        return (Boolean) attributes.get("email_verified");
    }

    @Override
    public String getUserName() {
        return (String) attributes.get("email");
    }

    @Override
    public String getFirstName() {
        return (String) attributes.get("given_name");
    }

    @Override
    public String getLastName() {
        return (String) attributes.get("family_name");
    }

    @Override
    public String getProvider() {
        return (String) AuthProvider.google.name();
    }

    @Override
    public String getProviderId() {
        return null;
    }

    @Override
    public AuthProvider getAuthProvider() {
        return AuthProvider.google;
    }

    @Override
    public Role getRole() {
        return new Role(100, "USER", true);
    }
}
