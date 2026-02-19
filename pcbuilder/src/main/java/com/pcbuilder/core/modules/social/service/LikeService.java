package com.pcbuilder.core.modules.social.service;

import com.pcbuilder.core.modules.auth.userdetails.UserPrincipal;
import com.pcbuilder.core.modules.build.model.Build;
import com.pcbuilder.core.modules.build.service.BuildProvider;
import com.pcbuilder.core.modules.social.dto.LikeResponseDto;
import com.pcbuilder.core.modules.social.model.Like;
import com.pcbuilder.core.modules.social.repository.LikeRepository;
import com.pcbuilder.core.modules.user.model.UserEntity;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final BuildProvider buildProvider;
    private final UserRepository userRepository;

    @Transactional
    public LikeResponseDto toggleLike(Long buildId, UserPrincipal userPrincipal) {
        Build build = buildProvider.getBuildById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found: " + buildId));

        if (build.isPrivate() && !build.getUser().getId().equals(userPrincipal.getId())) {
            throw new AccessDeniedException("Cannot like a private build");
        }

        Optional<Like> existingLike = likeRepository.findByUserIdAndBuildId(
                userPrincipal.getId(), buildId
        );

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            UserEntity user = userRepository.getReferenceById(userPrincipal.getId());

            Like like = new Like();
            like.setUser(user);
            like.setBuild(build);
            likeRepository.save(like);
        }

        long likeCount = likeRepository.countByBuildId(buildId);
        boolean liked = !existingLike.isPresent();

        return new LikeResponseDto(buildId, likeCount, liked);
    }

    @Transactional(readOnly = true)
    public LikeResponseDto getLikeStatus(Long buildId, UserPrincipal userPrincipal) {
        if (buildProvider.getBuildById(buildId).isEmpty()) {
            throw new EntityNotFoundException("Build not found: " + buildId);
        }

        long likeCount = likeRepository.countByBuildId(buildId);
        boolean liked = userPrincipal != null
                && likeRepository.existsByUserIdAndBuildId(userPrincipal.getId(), buildId);

        return new LikeResponseDto(buildId, likeCount, liked);
    }
}