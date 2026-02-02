package com.pcbuilder.core.modules.components.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeDto {
    private String name;
    private String value;
    private String unit;
}
