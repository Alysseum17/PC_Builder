package com.pcbuilder.core.modules.social.service;

import com.pcbuilder.core.modules.auth.userdetails.UserPrincipal;
import com.pcbuilder.core.modules.build.service.BuildProvider;
import com.pcbuilder.core.modules.social.dto.LikeResponseDto;
import com.pcbuilder.core.modules.social.model.Like;
import com.pcbuilder.core.modules.social.repository.LikeRepository;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    public Optional<LikeResponseDto> toggleLike(Long buildId, UserPrincipal userPrincipal) {
        return buildProvider.getBuildById(buildId)
                .filter(build -> !build.isPrivate() || build.getUser().getId().equals(userPrincipal.getId()))
                .map(build -> {
                    Optional<Like> existingLike = likeRepository.findByUserIdAndBuildId(
                            userPrincipal.getId(), buildId
                    );

                    if (existingLike.isPresent()) {
                        likeRepository.delete(existingLike.get());
                    } else {
                        Like like = new Like();
                        like.setUser(userRepository.getReferenceById(userPrincipal.getId()));
                        like.setBuild(build);
                        likeRepository.save(like);
                    }

                    long likeCount = likeRepository.countByBuildId(buildId);
                    return new LikeResponseDto(buildId, likeCount, existingLike.isEmpty());
                });
    }

    @Transactional(readOnly = true)
    public Optional<LikeResponseDto> getLikeStatus(Long buildId, UserPrincipal userPrincipal) {
        return buildProvider.getBuildById(buildId)
                .map(build -> {
                    long likeCount = likeRepository.countByBuildId(buildId);
                    boolean liked = userPrincipal != null
                            && likeRepository.existsByUserIdAndBuildId(userPrincipal.getId(), buildId);
                    return new LikeResponseDto(buildId, likeCount, liked);
                });
    }
}