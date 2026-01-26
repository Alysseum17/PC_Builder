package com.pcbuilder.core.modules.auth.controller;

import com.pcbuilder.core.modules.auth.dto.MailRequest;
import com.pcbuilder.core.modules.auth.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        return emailVerificationService.verifyEmail(token)
                .map(response -> ResponseEntity.ok(response.getMessage()))
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@Valid @RequestBody MailRequest request) {
        return emailVerificationService.resendVerification(request.getEmail())
                .map(response -> ResponseEntity.ok(response.getMessage()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }
}
