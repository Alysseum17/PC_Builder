package com.pcbuilder.core.modules.components.dto;

import lombok.Data;

import java.util.List;

@Data
public class ComponentResponseDto {
    private Long id;
    private String name;
    private String slug;
    private Double price;
    private String imageUrl;
    private String categoryName;
    private String categorySlug;
    private List<AttributeDto> attributes;
}
