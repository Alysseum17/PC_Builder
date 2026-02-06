package com.pcbuilder.core.modules.components.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ComponentRequestDto {

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 150, message = "Name must be between 3 and 150 chars")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @URL(message = "Invalid image URL format")
    private String imageUrl;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Valid
    private List<AttributeDto> attributes;
}
