package com.pcbuilder.core.modules.social.controller;

import com.pcbuilder.core.modules.auth.dto.MessageResponse;
import com.pcbuilder.core.modules.auth.userdetails.UserPrincipal;
import com.pcbuilder.core.modules.build.dto.BuildResponseDto;
import com.pcbuilder.core.modules.social.dto.BookmarkResponseDto;
import com.pcbuilder.core.modules.social.dto.CommentResponseDto;
import com.pcbuilder.core.modules.social.dto.CreateCommentRequest;
import com.pcbuilder.core.modules.social.dto.LikeResponseDto;
import com.pcbuilder.core.modules.social.service.BookmarkService;
import com.pcbuilder.core.modules.social.service.CommentService;
import com.pcbuilder.core.modules.social.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialController {

    private final CommentService commentService;
    private final LikeService likeService;
    private final BookmarkService bookmarkService;


    @PostMapping("/builds/{buildId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long buildId,
            @RequestBody @Valid CreateCommentRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return commentService.createComment(buildId, request, user)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/builds/{buildId}/comments")
    public ResponseEntity<Page<CommentResponseDto>> getComments(
            @PathVariable Long buildId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return commentService.getCommentsByBuild(buildId, pageable)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return commentService.deleteComment(commentId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/builds/{buildId}/likes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LikeResponseDto> toggleLike(
            @PathVariable Long buildId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return likeService.toggleLike(buildId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/builds/{buildId}/likes")
    public ResponseEntity<LikeResponseDto> getLikeStatus(
            @PathVariable Long buildId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return likeService.getLikeStatus(buildId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/builds/{buildId}/bookmarks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookmarkResponseDto> toggleBookmark(
            @PathVariable Long buildId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return bookmarkService.toggleBookmark(buildId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/builds/{buildId}/bookmarks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookmarkResponseDto> getBookmarkStatus(
            @PathVariable Long buildId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return bookmarkService.getBookmarkStatus(buildId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bookmarks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BuildResponseDto>> getMyBookmarks(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(bookmarkService.getMyBookmarks(user));
    }
}