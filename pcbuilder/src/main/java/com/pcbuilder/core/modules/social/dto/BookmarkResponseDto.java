package com.pcbuilder.core.modules.social.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookmarkResponseDto {
    private Long buildId;
    private boolean bookmarkedByCurrentUser;
}