package com.pcbuilder.core.modules.auth.repository;

import com.pcbuilder.core.modules.auth.model.EmailVerificationToken;
import com.pcbuilder.core.modules.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByUser(UserEntity user);
    @Modifying
    void deleteAllByExpiryDateBefore(LocalDateTime now);
    @Modifying
    void deleteByUser(UserEntity user);
}
