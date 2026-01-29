package com.pcbuilder.core.modules.auth.controller;

import com.pcbuilder.core.modules.auth.dto.Enable2FAResponse;
import com.pcbuilder.core.modules.auth.dto.JwtResponse;
import com.pcbuilder.core.modules.auth.dto.Verify2FALoginRequest;
import com.pcbuilder.core.modules.auth.dto.Verify2FASetupRequest;
import com.pcbuilder.core.modules.auth.service.TwoFactorService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<JwtResponse> verifyTwoFactorLogin(@Valid @RequestBody Verify2FALoginRequest request, HttpServletResponse response) {

        return twoFactorService.verify2FALogin(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}