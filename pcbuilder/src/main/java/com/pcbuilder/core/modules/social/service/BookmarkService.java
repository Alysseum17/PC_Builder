package com.pcbuilder.core.modules.social.service;

import com.pcbuilder.core.modules.auth.userdetails.UserPrincipal;
import com.pcbuilder.core.modules.build.dto.BuildResponseDto;
import com.pcbuilder.core.modules.build.mapper.BuildMapper;
import com.pcbuilder.core.modules.build.service.BuildProvider;
import com.pcbuilder.core.modules.social.dto.BookmarkResponseDto;
import com.pcbuilder.core.modules.social.model.Bookmark;
import com.pcbuilder.core.modules.social.repository.BookmarkRepository;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    public Optional<BookmarkResponseDto> toggleBookmark(Long buildId, UserPrincipal userPrincipal) {
        return buildProvider.getBuildById(buildId)
                .filter(build -> !build.isPrivate() || build.getUser().getId().equals(userPrincipal.getId()))
                .map(build -> {
                    Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserIdAndBuildId(
                            userPrincipal.getId(), buildId
                    );

                    if (existingBookmark.isPresent()) {
                        bookmarkRepository.delete(existingBookmark.get());
                        return new BookmarkResponseDto(buildId, false);
                    } else {
                        Bookmark bookmark = new Bookmark();
                        bookmark.setUser(userRepository.getReferenceById(userPrincipal.getId()));
                        bookmark.setBuild(build);
                        bookmarkRepository.save(bookmark);
                        return new BookmarkResponseDto(buildId, true);
                    }
                });
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
    public Optional<BookmarkResponseDto> getBookmarkStatus(Long buildId, UserPrincipal userPrincipal) {
        return buildProvider.getBuildById(buildId)
                .map(build -> {
                    boolean bookmarked = bookmarkRepository.existsByUserIdAndBuildId(
                            userPrincipal.getId(), buildId
                    );
                    return new BookmarkResponseDto(buildId, bookmarked);
                });
    }
}