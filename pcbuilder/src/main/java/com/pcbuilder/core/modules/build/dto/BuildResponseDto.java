package com.pcbuilder.core.modules.build.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BuildResponseDto {
    private Long id;
    private String name;
    private BigDecimal totalPrice;
    private List<BuildItemDto> items;
}
