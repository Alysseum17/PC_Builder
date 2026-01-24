package com.pcbuilder.core.modules.auth.controller;

import com.pcbuilder.core.modules.auth.dto.*;
import com.pcbuilder.core.modules.auth.service.AuthService;
import com.pcbuilder.core.modules.auth.utils.CookieUtils;
import com.pcbuilder.core.modules.auth.utils.MailService;
import jakarta.servlet.http.HttpServletRequest;
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
            CookieUtils.addCookie(response, "authToken", jwtResponse.getAuthToken(), 60*15);
            CookieUtils.addCookie(response, "refreshToken", jwtResponse.getRefreshToken(), 7*24*60*60);
            return ResponseEntity.ok("You have been logged in");
        }else if(result instanceof TwoFactorRequiredResponse twoFactorRequiredResponse) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(twoFactorRequiredResponse.getMessage());
        }
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
       CookieUtils.deleteCookie(request, response, "authToken");
       CookieUtils.deleteCookie(request, response, "refreshToken");

        return ResponseEntity.ok("You have been logged out");

    }
}

