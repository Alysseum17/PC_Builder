package com.pcbuilder.core.modules.social.service;

import com.pcbuilder.core.modules.auth.userdetails.UserPrincipal;
import com.pcbuilder.core.modules.build.dto.BuildResponseDto;
import com.pcbuilder.core.modules.build.mapper.BuildMapper;
import com.pcbuilder.core.modules.build.model.Build;
import com.pcbuilder.core.modules.build.service.BuildProvider;
import com.pcbuilder.core.modules.social.dto.BookmarkResponseDto;
import com.pcbuilder.core.modules.social.model.Bookmark;
import com.pcbuilder.core.modules.social.repository.BookmarkRepository;
import com.pcbuilder.core.modules.user.model.UserEntity;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BuildProvider buildProvider;
    private final UserRepository userRepository;
    private final BuildMapper buildMapper;
    private final SocialStatsService socialStatsService;

    @Transactional
    public BookmarkResponseDto toggleBookmark(Long buildId, UserPrincipal userPrincipal) {
        Build build = buildProvider.getBuildById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found: " + buildId));

        if (build.isPrivate() && !build.getUser().getId().equals(userPrincipal.getId())) {
            throw new AccessDeniedException("Cannot bookmark a private build");
        }

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserIdAndBuildId(
                userPrincipal.getId(), buildId
        );

        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
            return new BookmarkResponseDto(buildId, false);
        } else {
            UserEntity user = userRepository.getReferenceById(userPrincipal.getId());

            Bookmark bookmark = new Bookmark();
            bookmark.setUser(user);
            bookmark.setBuild(build);
            bookmarkRepository.save(bookmark);

            return new BookmarkResponseDto(buildId, true);
        }
    }

    @Transactional(readOnly = true)
    public List<BuildResponseDto> getMyBookmarks(UserPrincipal userPrincipal) {
        List<BuildResponseDto> dtos = bookmarkRepository
                .findAllByUserIdOrderByCreatedAtDesc(userPrincipal.getId())
                .stream()
                .map(bookmark -> buildMapper.toDto(bookmark.getBuild()))
                .toList();

        socialStatsService.enrichList(dtos, userPrincipal.getId());
        return dtos;
    }

    @Transactional(readOnly = true)
    public BookmarkResponseDto getBookmarkStatus(Long buildId, UserPrincipal userPrincipal) {
        if (buildProvider.getBuildById(buildId).isEmpty()) {
            throw new EntityNotFoundException("Build not found: " + buildId);
        }

        boolean bookmarked = userPrincipal != null
                && bookmarkRepository.existsByUserIdAndBuildId(userPrincipal.getId(), buildId);

        return new BookmarkResponseDto(buildId, bookmarked);
    }
}