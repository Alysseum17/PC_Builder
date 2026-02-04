package com.pcbuilder.core.modules.components.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ComponentFilterRequestDto {
    String category;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    String searchQuery;
    Map<String, String> attributes;
}
