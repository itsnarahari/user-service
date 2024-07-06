package com.user.security.oauth2.user;

import com.user.dto.AuthProvider;
import com.user.entities.Role;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
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
        return (String) attributes.get("avatar_url");
    }

    @Override
    public Boolean getEmailVerified() {
        return true;
    }

    @Override
    public String getUserName() {
        return (String) attributes.get("login");
    }

    @Override
    public String getFirstName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public String getProvider() {
        return AuthProvider.github.name();
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public AuthProvider getAuthProvider() {
        return AuthProvider.github;
    }
    @Override
    public Role getRole() {
        return new Role(100, "USER", true);
    }
}
