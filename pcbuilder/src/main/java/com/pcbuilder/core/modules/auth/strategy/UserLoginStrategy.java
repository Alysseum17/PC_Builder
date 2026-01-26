package com.pcbuilder.core.modules.auth.strategy;

import com.pcbuilder.core.modules.auth.dto.AuthResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface UserLoginStrategy {
    boolean supports(AuthResult result);
    ResponseEntity<?> handle(AuthResult result, HttpServletResponse response);
}
