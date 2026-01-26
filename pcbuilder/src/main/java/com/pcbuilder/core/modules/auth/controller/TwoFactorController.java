package com.pcbuilder.core.modules.auth.controller;

import com.pcbuilder.core.modules.auth.dto.Enable2FAResponse;
import com.pcbuilder.core.modules.auth.dto.Verify2FALoginRequest;
import com.pcbuilder.core.modules.auth.dto.Verify2FASetupRequest;
import com.pcbuilder.core.modules.auth.service.TwoFactorService;
import com.pcbuilder.core.modules.auth.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {

    private final TwoFactorService twoFactorService;
    static private final int AUTH_TOKEN_EXPIRY = 15 * 60;
    static private final int REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60;


    @PostMapping("/enable")
    public ResponseEntity<Enable2FAResponse> enableTwoFactor(Authentication authentication) {
        return twoFactorService.enable2FA(authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/verify-setup")
    public ResponseEntity<String> verifyTwoFactorSetup(Authentication authentication, @Valid @RequestBody Verify2FASetupRequest request) {
        return twoFactorService.verify2FASetup(authentication.getName(), request)
                .map(response -> ResponseEntity.ok(response.getMessage()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/disable")
    public ResponseEntity<String> disableTwoFactor(Authentication authentication, @RequestParam String code) {
        return twoFactorService.disable2FA(authentication.getName(), code)
                .map(response -> ResponseEntity.ok(response.getMessage()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/verify-login")
    public ResponseEntity<?> verifyTwoFactorLogin(@Valid @RequestBody Verify2FALoginRequest request, HttpServletResponse response) {

        return twoFactorService.verify2FALogin(request)
                .map(jwtResponse -> {
                    CookieUtils.addCookie(response, "authToken", jwtResponse.getAuthToken(), AUTH_TOKEN_EXPIRY);
                    CookieUtils.addCookie(response, "refreshToken", jwtResponse.getRefreshToken(), REFRESH_TOKEN_EXPIRY);

                    return ResponseEntity.ok("You have been logged in");
                })
                .orElse(ResponseEntity.status(401).body("User not found or invalid token"));
    }
}