package com.pcbuilder.core.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse implements AuthResult {
    private String accessToken;
    private String refreshToken;

    @Override
    public ResponseEntity<AuthResult> toResponseEntity() {
        return ResponseEntity.ok(this);
    }
}
