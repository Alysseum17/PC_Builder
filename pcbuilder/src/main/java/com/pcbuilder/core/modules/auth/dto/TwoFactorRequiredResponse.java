package com.pcbuilder.core.modules.auth.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class TwoFactorRequiredResponse implements AuthResult {
    private String message;
    private String tempToken;

    @Override
    public ResponseEntity<AuthResult> toResponseEntity() {
        return ResponseEntity.ok(this);
    }
}
