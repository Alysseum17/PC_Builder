package com.pcbuilder.core.modules.build.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddComponentRequest {
    @NotNull(message = "Component ID is required")
    @Positive
    private Long componentId;
}
