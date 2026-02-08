package com.pcbuilder.core.modules.build.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateBuildRequest {
    @NotBlank(message = "Build name cannot be empty")
    @Size(min = 3, max = 50, message = "Build name must be between 3 and 50 characters")
    private String name;

    private boolean isPrivate = false;
}