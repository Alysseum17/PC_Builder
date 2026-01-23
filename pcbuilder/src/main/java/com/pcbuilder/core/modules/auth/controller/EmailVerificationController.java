package com.pcbuilder.core.modules.auth.controller;

import com.pcbuilder.core.modules.auth.dto.MailDto;
import com.pcbuilder.core.modules.auth.dto.MessageResponseDto;
import com.pcbuilder.core.modules.auth.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        MessageResponseDto message = emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok(message.getMessage());
    }
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@Valid @RequestBody MailDto request) throws Exception {
        MessageResponseDto message = emailVerificationService.resendVerification(request.getEmail());
        return ResponseEntity.ok(message.getMessage());
    }
}
