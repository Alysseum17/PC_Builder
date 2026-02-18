package com.pcbuilder.core.modules.components.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeValueDto {
    private String value;
    private Long count;
}
