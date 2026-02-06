package com.pcbuilder.core.modules.build.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BuildItemDto {
    private Long id;
    private Long componentId;
    private BigDecimal price;


    private String componentName;
    private String imageUrl;
    private String categorySlug;
}

