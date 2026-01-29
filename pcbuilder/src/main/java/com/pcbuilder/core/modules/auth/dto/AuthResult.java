package com.pcbuilder.core.modules.auth.dto;

import org.springframework.http.ResponseEntity;

public interface AuthResult {
    public ResponseEntity<AuthResult> toResponseEntity();
}
