package com.pcbuilder.core.modules.social.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    private Long buildId;
    private String authorUsername;
    private String authorAvatarFileName;
}