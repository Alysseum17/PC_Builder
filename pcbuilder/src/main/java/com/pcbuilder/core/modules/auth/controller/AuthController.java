package com.pcbuilder.core.modules.auth.controller;

import com.pcbuilder.core.modules.auth.dto.*;
import com.pcbuilder.core.modules.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerDto) throws Exception {
        MessageResponse message = authService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(message.getMessage());
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResult> login(@Valid @RequestBody LoginRequest loginDto, HttpServletResponse response) throws Exception {
        return authService.login(loginDto).map(AuthResult::toResponseEntity)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResult> refreshToken(@Valid @RequestBody RefreshTokenRequest request, HttpServletResponse response) throws Exception {
        return authService.refreshToken(request).map(AuthResult::toResponseEntity)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

}

