package com.pcbuilder.core.modules.auth.service;

import com.pcbuilder.core.modules.auth.dto.JwtResponseDto;
import com.pcbuilder.core.modules.auth.dto.LoginRequestDto;
import com.pcbuilder.core.modules.auth.dto.MessageResponseDto;
import com.pcbuilder.core.modules.auth.dto.RegisterRequestDto;
import com.pcbuilder.core.modules.auth.jwt.JwtTokenProvider;
import com.pcbuilder.core.modules.user.model.UserEntity;
import com.pcbuilder.core.modules.user.model.UserRole;
import com.pcbuilder.core.modules.user.model.UserStatus;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailVerificationService emailVerificationService;

    public MessageResponseDto registerUser(RegisterRequestDto request) throws Exception {
        if (isUsernameTaken(request.getUsername())) {
            throw new Exception("Username is already taken");
        }
        if (isEmailTaken(request.getEmail())) {
            throw new Exception("Email is already registered");
        }
        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new Exception("Passwords do not match");
        }
        HashSet<UserRole> userRoles = new HashSet<>();
        userRoles.add(UserRole.USER);
        UserEntity userEntity = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .hash_password(passwordEncoder.encode(request.getPassword()))
                .roles(userRoles)
                .status(UserStatus.ACTIVE)
                .build();
        emailVerificationService.createEmailVerificationToken(userEntity);
        userRepository.save(userEntity);

        return new MessageResponseDto("User registered successfully");
    }

    public JwtResponseDto login(LoginRequestDto request) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
        ));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String authToken = jwtTokenProvider.generateToken(authentication);
    String refreshToken = jwtTokenProvider.generateRefreshToken(request.getUsername());

    return JwtResponseDto.builder().authToken(authToken).refreshToken(refreshToken).build();

    }




    private boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }
    private boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }
}
