package com.pcbuilder.core.modules.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TwoFactorRequiredResponse {
    private String message;
    private String tempToken;
}
