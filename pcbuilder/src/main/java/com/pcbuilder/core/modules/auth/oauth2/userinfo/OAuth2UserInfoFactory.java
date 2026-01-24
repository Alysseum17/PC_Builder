package com.pcbuilder.core.modules.auth.oauth2.userinfo;

import com.pcbuilder.core.modules.user.model.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.FACEBOOK.toString())) {
            return new FacebookOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.toString())) {
            return new GitHubOAuth2UserInfo(attributes);
        } else {
            throw new IllegalArgumentException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
