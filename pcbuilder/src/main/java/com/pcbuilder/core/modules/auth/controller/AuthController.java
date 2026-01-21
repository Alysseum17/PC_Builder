package com.pcbuilder.core.modules.auth.controller;

import com.pcbuilder.core.modules.auth.dto.MessageResponseDto;
import com.pcbuilder.core.modules.auth.dto.RegisterRequestDto;
import com.pcbuilder.core.modules.auth.service.AuthService;
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
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto registerDto) throws Exception {
        MessageResponseDto message = authService.registerUser(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(message.getMessage());
    }
}
