package com.pcbuilder.core.modules.auth.service;

import com.pcbuilder.core.modules.auth.dto.MailRequest;
import com.pcbuilder.core.modules.auth.dto.MessageResponse;
import com.pcbuilder.core.modules.auth.dto.ResetPasswordRequest;
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
import java.util.Optional;
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

    public MessageResponse forgotPassword(MailRequest request) {
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
        return new MessageResponse("Password reset email sent");
    }

    public Optional<MessageResponse> resetPassword(ResetPasswordRequest request, String token) {
        return resetPasswordRepository.findByToken(token)
                .map(resetToken -> {
                    if (resetToken.isExpired() || resetToken.isUsed()) {
                        throw new IllegalStateException("Token is invalid or expired");
                    }

                    if (!request.getPassword().equals(request.getConfirmPassword())) {
                        throw new IllegalArgumentException("Passwords do not match");
                    }

                    UserEntity user = resetToken.getUser();
                    user.setHash_password(passwordEncoder.encode(request.getPassword()));
                    userRepository.save(user);

                    resetToken.setUsed(true);
                    resetPasswordRepository.save(resetToken);

                    return new MessageResponse("Password has been reset successfully");
                });
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void deleteExpiredTokens() {
        resetPasswordRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }
}