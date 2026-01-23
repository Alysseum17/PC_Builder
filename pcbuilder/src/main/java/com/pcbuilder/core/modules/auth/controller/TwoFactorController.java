package com.pcbuilder.core.modules.auth.controller;

import com.pcbuilder.core.modules.auth.dto.*;
import com.pcbuilder.core.modules.auth.service.TwoFactorService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {
    private final TwoFactorService twoFactorService;
    @PostMapping("/enable")
    public ResponseEntity<Enable2FAResponse> enableTwoFactor(Authentication authentication) {
        String username = authentication.getName();
        Enable2FAResponse response = twoFactorService.enable2FA(username);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/verify-setup")
    public ResponseEntity<String> verifyTwoFactorSetup(Authentication authentication, @Valid @RequestBody Verify2FASetupRequest request)  {
        String username = authentication.getName();
        MessageResponse response = twoFactorService.verify2FASetup(username, request);
        return ResponseEntity.ok(response.getMessage());
    }
    @PostMapping("/disable")
    public ResponseEntity<String> disableTwoFactor(Authentication authentication, @RequestParam String code) {
        String username = authentication.getName();
        MessageResponse response = twoFactorService.disable2FA(username, code);
        return ResponseEntity.ok(response.getMessage());
    }

    @PostMapping("verify-login")
    public ResponseEntity<String> verifyTwoFactorLogin(@Valid @RequestBody Verify2FALoginRequest request, HttpServletResponse response) {
        JwtResponse jwtResponse = twoFactorService.verify2FALogin(request);
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
    }
}
