package com.pcbuilder.core.modules.auth.controller;

import com.pcbuilder.core.modules.auth.dto.*;
import com.pcbuilder.core.modules.auth.service.AuthService;
import com.pcbuilder.core.modules.auth.utils.MailService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    private final MailService mailService;
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerDto) throws Exception {
        MessageResponse message = authService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(message.getMessage());
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginDto, HttpServletResponse response) throws Exception {
        Object result = authService.login(loginDto);
        if(result instanceof JwtResponse jwtResponse) {
            ResponseCookie authCookie = ResponseCookie.from("authToken", jwtResponse.getAuthToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(60 * 15)
                    .sameSite("Strict")
                    .build();
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", jwtResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, authCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return ResponseEntity.ok("You have been logged in");
        }else if(result instanceof TwoFactorRequiredResponse twoFactorRequiredResponse) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(twoFactorRequiredResponse.getMessage());
        }
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        ResponseCookie authCookie = ResponseCookie.from("authToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, authCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok("You have been logged out");

    }
}

