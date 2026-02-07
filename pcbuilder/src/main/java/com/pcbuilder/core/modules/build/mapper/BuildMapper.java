package com.pcbuilder.core.modules.build.mapper;

import com.pcbuilder.core.modules.build.dto.BuildItemDto;
import com.pcbuilder.core.modules.build.dto.BuildResponseDto;
import com.pcbuilder.core.modules.build.model.Build;
import com.pcbuilder.core.modules.build.model.BuildItem;
import com.pcbuilder.core.modules.components.service.ComponentProvider;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class BuildMapper {

    @Autowired
    protected ComponentProvider componentProvider;

    public abstract BuildResponseDto toDto(Build build);

    @Mapping(target = "price", source = "priceSnapshot")
    @Mapping(target = "componentName", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "categorySlug", ignore = true)
    public abstract BuildItemDto toItemDto(BuildItem item);

    @AfterMapping
    protected void enrichItemDto(BuildItem item, @MappingTarget BuildItemDto dto) {
        try {
            var component = componentProvider.getComponentById(item.getComponentId());
            dto.setComponentName(component.getName());
            dto.setImageUrl(component.getImageUrl());
            if (component.getCategory() != null) {
                dto.setCategorySlug(component.getCategory().getSlug());
            }
        } catch (Exception e) {
            dto.setComponentName("Unknown Component");
        }
    }
}
