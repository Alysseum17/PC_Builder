package com.pcbuilder.core.modules.build.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReplaceComponentRequest {
    @NotNull(message = "New component ID is required")
    private Long newComponentId;
}
