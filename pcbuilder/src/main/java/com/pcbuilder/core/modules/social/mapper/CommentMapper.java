package com.pcbuilder.core.modules.social.mapper;

import com.pcbuilder.core.modules.social.dto.CommentResponseDto;
import com.pcbuilder.core.modules.social.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "buildId", source = "build.id")
    @Mapping(target = "authorUsername", source = "user.username")
    @Mapping(target = "authorAvatarFileName", source = "user.avatarFileName")
    CommentResponseDto toDto(Comment comment);
}