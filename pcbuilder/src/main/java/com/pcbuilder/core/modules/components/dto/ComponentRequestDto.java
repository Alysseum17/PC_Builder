package com.pcbuilder.core.modules.components.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ComponentRequestDto {
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private Long categoryId;

    private List<AttributeDto> attributes;
}
