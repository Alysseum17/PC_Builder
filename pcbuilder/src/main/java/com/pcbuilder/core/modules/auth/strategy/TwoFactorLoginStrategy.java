package com.pcbuilder.core.modules.auth.strategy;

import com.pcbuilder.core.modules.auth.dto.AuthResult;
import com.pcbuilder.core.modules.auth.dto.TwoFactorRequiredResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TwoFactorLoginStrategy implements UserLoginStrategy {
    @Override
    public boolean supports(AuthResult result) {
        return result instanceof TwoFactorRequiredResponse;
    }

    @Override
    public ResponseEntity<?> handle(AuthResult result, HttpServletResponse response) {
        TwoFactorRequiredResponse twoFactorResponse = (TwoFactorRequiredResponse) result;
        return ResponseEntity.ok(twoFactorResponse);
    }
}
