package com.pcbuilder.core.modules.auth.controller;

import com.pcbuilder.core.modules.auth.dto.MailRequest;
import com.pcbuilder.core.modules.auth.dto.MessageResponse;
import com.pcbuilder.core.modules.auth.dto.ResetPasswordRequest;
import com.pcbuilder.core.modules.auth.service.ResetPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class ResetPasswordController {
    private final ResetPasswordService resetPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody MailRequest request) {
        MessageResponse message = resetPasswordService.forgotPassword(request);
        return ResponseEntity.ok(message.getMessage());
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request, @RequestParam String token) {
        MessageResponse message = resetPasswordService.resetPassword(request, token);
        return ResponseEntity.ok(message.getMessage());
    }
}
