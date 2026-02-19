package com.pcbuilder.core.modules.social.service;

import com.pcbuilder.core.modules.auth.dto.MessageResponse;
import com.pcbuilder.core.modules.auth.userdetails.UserPrincipal;
import com.pcbuilder.core.modules.build.service.BuildProvider;
import com.pcbuilder.core.modules.social.dto.CommentResponseDto;
import com.pcbuilder.core.modules.social.dto.CreateCommentRequest;
import com.pcbuilder.core.modules.social.mapper.CommentMapper;
import com.pcbuilder.core.modules.social.model.Comment;
import com.pcbuilder.core.modules.social.repository.CommentRepository;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BuildProvider buildProvider;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public Optional<CommentResponseDto> createComment(Long buildId, CreateCommentRequest request, UserPrincipal userPrincipal) {
        return buildProvider.getBuildById(buildId)
                .filter(build -> !build.isPrivate() || build.getUser().getId().equals(userPrincipal.getId()))
                .map(build -> {
                    Comment comment = new Comment();
                    comment.setUser(userRepository.getReferenceById(userPrincipal.getId()));
                    comment.setBuild(build);
                    comment.setContent(request.getContent());
                    return commentMapper.toDto(commentRepository.save(comment));
                });
    }

    @Transactional(readOnly = true)
    public Optional<Page<CommentResponseDto>> getCommentsByBuild(Long buildId, Pageable pageable) {
        return buildProvider.getBuildById(buildId)
                .map(build -> commentRepository.findByBuildIdOrderByCreatedAtDesc(buildId, pageable)
                        .map(commentMapper::toDto));
    }

    @Transactional
    public Optional<MessageResponse> deleteComment(Long commentId, UserPrincipal userPrincipal) {
        return commentRepository.findById(commentId)
                .filter(comment -> {
                    boolean isAuthor = comment.getUser().getId().equals(userPrincipal.getId());
                    boolean isBuildOwner = comment.getBuild().getUser().getId().equals(userPrincipal.getId());
                    return isAuthor || isBuildOwner;
                })
                .map(comment -> {
                    commentRepository.delete(comment);
                    return new MessageResponse("Comment deleted successfully");
                });
    }
}