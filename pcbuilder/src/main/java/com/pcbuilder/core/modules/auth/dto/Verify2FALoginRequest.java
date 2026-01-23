package com.pcbuilder.core.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Verify2FALoginRequest {
    @NotBlank(message = "Verification code is required")
    private String code;
    @NotBlank(message = "Temporary token is required")
    private String tempToken;
}
