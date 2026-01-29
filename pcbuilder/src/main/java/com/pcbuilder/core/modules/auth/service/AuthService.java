package com.pcbuilder.core.modules.auth.service;

import com.pcbuilder.core.modules.auth.dto.*;
import com.pcbuilder.core.modules.auth.jwt.JwtTokenProvider;
import com.pcbuilder.core.modules.exception.DuplicateResourceException;
import com.pcbuilder.core.modules.exception.PasswordMismatchException;
import com.pcbuilder.core.modules.exception.TokenException;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailVerificationService emailVerificationService;

    public MessageResponse register(RegisterRequest request) throws DuplicateResourceException, PasswordMismatchException {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already in use");
        }
        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords don't match");
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
        userRepository.save(userEntity);
        emailVerificationService.createEmailVerificationToken(userEntity);

        return new MessageResponse("User registered successfully");
    }

    public Optional<AuthResult> login(LoginRequest request) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
        ));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return userRepository.findByUsernameOrEmail(request.getLogin(), request.getLogin()).map(userEntity -> {
       if (userEntity.isTwoFactorEnabled()) {
           String tempToken = jwtTokenProvider.generateTempToken(userEntity.getUsername());
           return TwoFactorRequiredResponse.builder()
                   .message("Need to verify 2FA with temp token" + tempToken)
                   .tempToken(tempToken)
                   .build();
       }
       String accessToken = jwtTokenProvider.generateToken(userEntity.getUsername());
       String refreshToken = jwtTokenProvider.generateRefreshToken(userEntity.getUsername());

       return JwtResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        });
    }
    public Optional<JwtResponse> refreshToken(RefreshTokenRequest request) throws Exception {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new TokenException("Invalid refresh token");
        }
        String username = jwtTokenProvider.getUsernameFromJWT(request.getRefreshToken());

        return userRepository.findByUsername(username)
                .map(user -> {
                    String newAccessToken = jwtTokenProvider.generateToken(username);
                    String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
                    return new JwtResponse(newAccessToken, newRefreshToken);
                });
    }
}
