    package com.pcbuilder.core.modules.auth.service;

    import com.pcbuilder.core.modules.auth.dto.MessageResponse;
    import com.pcbuilder.core.modules.auth.model.EmailVerificationToken;
    import com.pcbuilder.core.modules.auth.repository.EmailVerificationRepository;
    import com.pcbuilder.core.modules.auth.utils.MailService;
    import com.pcbuilder.core.modules.user.model.UserEntity;
    import com.pcbuilder.core.modules.user.repository.UserRepository;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.Optional;
    import java.util.UUID;

    @Service
    @RequiredArgsConstructor
    @Transactional
    public class EmailVerificationService {
        private final EmailVerificationRepository emailVerificationRepository;
        private final UserRepository userRepository;
        private final MailService mailService;
        private final static int EXPIRATION_MINUTES = 60;

        public MessageResponse createEmailVerificationToken(UserEntity user) {
            String token = UUID.randomUUID().toString();

            EmailVerificationToken emailVerificationToken = EmailVerificationToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES))
                    .build();
            emailVerificationRepository.save(emailVerificationToken);

            String verificationLink = "http://localhost:3000/verify-email?token=" + token;

            Map<String, Object> variables = new HashMap<>();
            variables.put("username", user.getUsername());
            variables.put("verificationLink", verificationLink);

            mailService.sendHtmlEmailAsync(user.getEmail(), "Please Verify Your Email", "verification", variables);

            return new MessageResponse("Verification email sent");
        }

        public Optional<MessageResponse> verifyEmail(String token) {
            return emailVerificationRepository.findByToken(token)
                    .map(emailToken -> {
                        if (emailToken.isExpired()) {
                            throw new IllegalStateException("Token is expired");
                        }
                        UserEntity user = emailToken.getUser();
                        user.setEmailVerified(true);
                        userRepository.save(user);
                        emailVerificationRepository.delete(emailToken);

                        Map<String, Object> variables = new HashMap<>();
                        variables.put("username", user.getUsername());
                        variables.put("loginLink", "http://localhost:3000/login");
                        mailService.sendHtmlEmailAsync(user.getEmail(), "Welcome to the Platform!", "welcome", variables);
                        return new MessageResponse("Email verified successfully");
                    });
        }

        public Optional<MessageResponse> resendVerification(String email) {
            return userRepository.findByEmail(email)
                    .map(user -> {
                        if (user.isEmailVerified()) {
                            throw new IllegalStateException("Email is already verified");
                        }
                        emailVerificationRepository.findByUser(user)
                                .ifPresent(emailVerificationRepository::delete);
                        createEmailVerificationToken(user);
                        return new MessageResponse("Email verification email resent");
                    });
        }
    }
