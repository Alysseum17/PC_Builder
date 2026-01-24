package com.pcbuilder.core.modules.auth.oauth2.service;

import com.pcbuilder.core.modules.auth.oauth2.userinfo.OAuth2UserInfo;
import com.pcbuilder.core.modules.auth.oauth2.userinfo.OAuth2UserInfoFactory;
import com.pcbuilder.core.modules.auth.userdetails.UserPrincipal;
import com.pcbuilder.core.modules.user.model.AuthProvider;
import com.pcbuilder.core.modules.user.model.UserEntity;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return proccessOAuth2User(oAuth2User, userRequest);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }

    }
    public OAuth2User proccessOAuth2User(OAuth2User oAuth2User, OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        if(!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }
        Optional<UserEntity> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        UserEntity user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!user.getProvider().equalsIgnoreCase(registrationId)) {
                throw new RuntimeException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        }else {
            user = registerNewUser(oAuth2UserInfo, registrationId);
        }
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }
    private UserEntity registerNewUser(OAuth2UserInfo oAuth2UserInfo, String registrationId) {
        UserEntity user = new UserEntity();
        user.setAuthProvider(AuthProvider.valueOf(registrationId));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setUsername(oAuth2UserInfo.getEmail());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setEmailVerified(true);
        user.setAvatar_url(oAuth2UserInfo.getImageUrl());
        return userRepository.save(user);
    }
    private UserEntity updateExistingUser(UserEntity existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setAvatar_url(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }
}
