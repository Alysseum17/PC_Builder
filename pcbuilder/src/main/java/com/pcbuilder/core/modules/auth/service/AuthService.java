package com.pcbuilder.core.modules.auth.service;

import com.pcbuilder.core.modules.auth.dto.RegisterDto;
import com.pcbuilder.core.modules.auth.repository.UserRepository;
import com.pcbuilder.core.modules.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity registerUser(RegisterDto request) throws Exception {
        if (isUsernameTaken(request.getUsername())) {
            throw new Exception("Username is already taken");
        }
        if (isEmailTaken(request.getEmail())) {
            throw new Exception("Email is already registered");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        userEntity.setEmail(request.getEmail());
        userEntity.setHash_password(passwordEncoder.encode(request.getPassword()));
        userRepository.save(userEntity);

        return userEntity;
    }

    private boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }
    private boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }
}
