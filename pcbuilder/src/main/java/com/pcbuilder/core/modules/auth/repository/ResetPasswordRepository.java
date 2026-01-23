package com.pcbuilder.core.modules.auth.repository;

import com.pcbuilder.core.modules.auth.model.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByToken(String token);
    @Modifying
    void deleteAllByExpiryDateBefore(LocalDateTime now);

}
