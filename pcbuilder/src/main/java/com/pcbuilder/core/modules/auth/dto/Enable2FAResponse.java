package com.pcbuilder.core.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Enable2FAResponse {
    private String secret;
    private String qrCodeUrl;
    private List<String> backupCodes;
}
