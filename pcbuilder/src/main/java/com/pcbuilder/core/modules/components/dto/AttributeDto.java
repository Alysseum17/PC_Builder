package com.pcbuilder.core.modules.components.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeDto {
    @NotBlank(message = "Attribute name is required")
    private String name;

    @NotBlank(message = "Attribute value is required")
    private String value;

    private String unit;
}
