package com.pcbuilder.core.modules.components.dto;

import com.pcbuilder.core.modules.validator.ValidPriceRange;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@ValidPriceRange
public class ComponentFilterRequestDto {

    @Size(max = 50, message = "Category slug is too long")
    private String category;

    @PositiveOrZero(message = "Min price cannot be negative")
    private BigDecimal minPrice;

    @Positive(message = "Max price must be greater than 0")
    private BigDecimal maxPrice;

    @Size(max = 100, message = "Search query is too long (max 100 chars)")
    private String searchQuery;

    private Map<String, String> attributes;
}
