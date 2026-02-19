package com.pcbuilder.core.modules.social.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeResponseDto {
    private Long buildId;
    private long likeCount;
    private boolean likedByCurrentUser;
}