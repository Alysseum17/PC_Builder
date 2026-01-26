package com.pcbuilder.core.modules.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TwoFactorRequiredResponse implements AuthResult {
    private String message;
    private String tempToken;
}
