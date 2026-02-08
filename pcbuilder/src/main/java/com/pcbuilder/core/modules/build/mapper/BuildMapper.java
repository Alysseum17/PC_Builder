package com.pcbuilder.core.modules.build.mapper;

import com.pcbuilder.core.modules.build.dto.BuildItemDto;
import com.pcbuilder.core.modules.build.dto.BuildResponseDto;
import com.pcbuilder.core.modules.build.model.Build;
import com.pcbuilder.core.modules.build.model.BuildItem;
import com.pcbuilder.core.modules.components.service.ComponentProvider;
import com.pcbuilder.core.modules.user.sevice.UserProvider;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class BuildMapper {

    @Autowired
    protected ComponentProvider componentProvider;

    @Autowired
    protected UserProvider userProvider;

    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "authorAvatar", ignore = true)
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

    @AfterMapping
    protected void enrichAuthorInfo(Build build, @MappingTarget BuildResponseDto dto) {
        userProvider.getUserSummary(build.getUserId())
                .ifPresent(user -> {
                    dto.setAuthorUsername(user.getUsername());
                    dto.setAuthorAvatarFileName(user.getAvatarFileName());
                });

        if (dto.getAuthorUsername() == null) {
            dto.setAuthorUsername("Deleted User");
        }
    }

}
