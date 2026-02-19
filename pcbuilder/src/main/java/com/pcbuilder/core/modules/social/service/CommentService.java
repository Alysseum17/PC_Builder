package com.pcbuilder.core.modules.social.service;

import com.pcbuilder.core.modules.auth.dto.MessageResponse;
import com.pcbuilder.core.modules.auth.userdetails.UserPrincipal;
import com.pcbuilder.core.modules.build.model.Build;
import com.pcbuilder.core.modules.build.service.BuildProvider;
import com.pcbuilder.core.modules.social.dto.CommentResponseDto;
import com.pcbuilder.core.modules.social.dto.CreateCommentRequest;
import com.pcbuilder.core.modules.social.mapper.CommentMapper;
import com.pcbuilder.core.modules.social.model.Comment;
import com.pcbuilder.core.modules.social.repository.CommentRepository;
import com.pcbuilder.core.modules.user.model.UserEntity;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BuildProvider buildProvider;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponseDto createComment(Long buildId, CreateCommentRequest request, UserPrincipal userPrincipal) {
        Build build = buildProvider.getBuildById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found: " + buildId));

        if (build.isPrivate() && !build.getUser().getId().equals(userPrincipal.getId())) {
            throw new AccessDeniedException("Cannot comment on a private build");
        }

        UserEntity user = userRepository.getReferenceById(userPrincipal.getId());

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setBuild(build);
        comment.setContent(request.getContent());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getCommentsByBuild(Long buildId, Pageable pageable) {
        if (buildProvider.getBuildById(buildId).isEmpty()) {
            throw new EntityNotFoundException("Build not found: " + buildId);
        }

        return commentRepository.findByBuildIdOrderByCreatedAtDesc(buildId, pageable)
                .map(commentMapper::toDto);
    }

    @Transactional
    public MessageResponse deleteComment(Long commentId, UserPrincipal userPrincipal) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found: " + commentId));

        boolean isAuthor = comment.getUser().getId().equals(userPrincipal.getId());
        boolean isBuildOwner = comment.getBuild().getUser().getId().equals(userPrincipal.getId());

        if (!isAuthor && !isBuildOwner) {
            throw new AccessDeniedException("You can only delete your own comments or comments on your builds");
        }

        commentRepository.delete(comment);
        return new MessageResponse("Comment deleted successfully");
    }
}