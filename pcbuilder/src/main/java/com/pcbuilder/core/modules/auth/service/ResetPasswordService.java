package com.pcbuilder.core.modules.auth.service;

import com.pcbuilder.core.modules.auth.dto.MailDto;
import com.pcbuilder.core.modules.auth.dto.MessageResponseDto;
import com.pcbuilder.core.modules.auth.dto.ResetPasswordDto;
import com.pcbuilder.core.modules.auth.model.ResetPasswordToken;
import com.pcbuilder.core.modules.auth.repository.ResetPasswordRepository;
import com.pcbuilder.core.modules.auth.utils.MailService;
import com.pcbuilder.core.modules.user.model.UserEntity;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ResetPasswordService {

    private final UserRepository userRepository;
    private final ResetPasswordRepository resetPasswordRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    private static final int EXPIRATION_HOURS = 24;

    public MessageResponseDto forgotPassword(MailDto request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            resetPasswordRepository.deleteByUser(user);
            String token = UUID.randomUUID().toString();

            ResetPasswordToken resetToken = ResetPasswordToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS))
                    .used(false)
                    .build();

            resetPasswordRepository.save(resetToken);

            String resetLink = "http://localhost:3000/reset-password?token=" + token;
            mailService.sendResetPasswordEmail(user.getEmail(), resetLink);

        });
        return new MessageResponseDto("Password reset email sent");
    }

    public MessageResponseDto resetPassword(ResetPasswordDto request, String token) {
        ResetPasswordToken resetToken = resetPasswordRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or missing token")); // Тут можна кидати помилку

        if (resetToken.isExpired() || resetToken.isUsed()) {
            throw new RuntimeException("Token is invalid");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        UserEntity user = resetToken.getUser();
        user.setHash_password(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        resetPasswordRepository.save(resetToken);
        return new MessageResponseDto("Password has been reset successfully");
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void deleteExpiredTokens() {
        resetPasswordRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }
}