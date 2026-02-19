package com.pcbuilder.core.modules.social.service;

import com.pcbuilder.core.modules.build.dto.BuildResponseDto;
import com.pcbuilder.core.modules.social.repository.BookmarkRepository;
import com.pcbuilder.core.modules.social.repository.CommentRepository;
import com.pcbuilder.core.modules.social.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialStatsService {

    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public void enrichOne(BuildResponseDto dto, Long currentUserId) {
        Long buildId = dto.getId();

        dto.setLikeCount(likeRepository.countByBuildId(buildId));
        dto.setCommentCount(commentRepository.countByBuildId(buildId));

        if (currentUserId != null) {
            dto.setLikedByCurrentUser(likeRepository.existsByUserIdAndBuildId(currentUserId, buildId));
            dto.setBookmarkedByCurrentUser(bookmarkRepository.existsByUserIdAndBuildId(currentUserId, buildId));
        }
    }

    @Transactional(readOnly = true)
    public void enrichList(List<BuildResponseDto> dtos, Long currentUserId) {
        if (dtos.isEmpty()) return;

        List<Long> buildIds = dtos.stream().map(BuildResponseDto::getId).toList();

        Map<Long, Long> likeCounts = likeRepository.countByBuildIds(buildIds)
                .stream().collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));

        Map<Long, Long> commentCounts = commentRepository.countByBuildIds(buildIds)
                .stream().collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));

        Set<Long> likedBuildIds = Set.of();
        Set<Long> bookmarkedBuildIds = Set.of();

        if (currentUserId != null) {
            likedBuildIds = likeRepository.findBuildIdsLikedByUser(currentUserId, buildIds)
                    .stream().collect(Collectors.toSet());
            bookmarkedBuildIds = bookmarkRepository.findBuildIdsBookmarkedByUser(currentUserId, buildIds)
                    .stream().collect(Collectors.toSet());
        }

        for (BuildResponseDto dto : dtos) {
            Long buildId = dto.getId();
            dto.setLikeCount(likeCounts.getOrDefault(buildId, 0L));
            dto.setCommentCount(commentCounts.getOrDefault(buildId, 0L));
            dto.setLikedByCurrentUser(likedBuildIds.contains(buildId));
            dto.setBookmarkedByCurrentUser(bookmarkedBuildIds.contains(buildId));
        }
    }
}