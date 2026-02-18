package com.pcbuilder.core.modules.build.mapper;

import com.pcbuilder.core.modules.build.dto.BuildItemDto;
import com.pcbuilder.core.modules.build.dto.BuildResponseDto;
import com.pcbuilder.core.modules.build.model.Build;
import com.pcbuilder.core.modules.build.model.BuildItem;
import com.pcbuilder.core.modules.components.service.ComponentProvider;
import com.pcbuilder.core.modules.user.service.UserProvider;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class BuildMapper {

    @Autowired
    protected ComponentProvider componentProvider;

    @Autowired
    protected UserProvider userProvider;

    @Mapping(target = "authorUsername", source = "user.username")
    @Mapping(target = "authorAvatarFileName", source = "user.avatarFileName")
    public abstract BuildResponseDto toDto(Build build);

    @Mapping(target = "price", source = "priceSnapshot")
    @Mapping(target = "componentId", source = "component.id")
    @Mapping(target = "componentName", source = "component.name")
    @Mapping(target = "imageUrl", source = "component.imageUrl")
    @Mapping(target = "categorySlug", source = "component.category.slug")
    public abstract BuildItemDto toItemDto(BuildItem item);

}
