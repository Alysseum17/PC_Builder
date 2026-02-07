package com.pcbuilder.core.modules.build.dto;

import com.pcbuilder.core.modules.validator.ValidPriceRange;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ValidPriceRange
public class BuildFilterRequestDto {
    
    @Size(max = 100)
    private String searchQuery;
    
    @PositiveOrZero
    private BigDecimal minPrice;
    
    @Positive
    private BigDecimal maxPrice;
    
    private String username;
}