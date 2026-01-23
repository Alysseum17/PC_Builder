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

            mailService.sendEmailVerificationEmail(user.getEmail(), verificationLink);

            return new MessageResponse("Verification email sent");
        }

        public MessageResponse verifyEmail(String token) {
            EmailVerificationToken emailToken = emailVerificationRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Invalid or missing token"));

            if (emailToken.isExpired()) {
                throw new RuntimeException("Token is expired");
            }

            UserEntity user = emailToken.getUser();
            user.setEmailVerified(true);
            userRepository.save(user);

            emailVerificationRepository.delete(emailToken);

            return new MessageResponse("Email verified successfully");
        }

        public MessageResponse resendVerification(String email) {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User with given email not found"));
            if(user.isEmailVerified()) {
                throw new RuntimeException("Email is already verified");
            }
            emailVerificationRepository.findByUser(user).ifPresent(emailVerificationRepository::delete);

            createEmailVerificationToken(user);

            return new MessageResponse("Email verification email resent");
        }
    }
