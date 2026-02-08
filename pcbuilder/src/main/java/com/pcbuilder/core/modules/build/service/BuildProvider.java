package com.pcbuilder.core.modules.build.service;

import com.pcbuilder.core.modules.build.model.Build;
import com.pcbuilder.core.modules.build.repository.BuildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuildProvider {
    private final BuildRepository buildRepository;

        public Optional<Build> getBuildByUserId(Long userId) {
            return buildRepository.findByUserId(userId);
        }
        public Optional<Build> getBuildById(Long buildId) {
            return buildRepository.findById(buildId);
        }

}
