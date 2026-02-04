package com.pcbuilder.core.modules.components.mapper;

import com.pcbuilder.core.modules.components.dto.ComponentResponseDto;
import com.pcbuilder.core.modules.components.model.Component;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ComponentMapper {
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categorySlug", source = "category.slug")
    ComponentResponseDto toDto(Component component);
}
