package com.pcbuilder.core.modules.build.service;

import com.pcbuilder.core.modules.auth.userdetails.UserPrincipal;
import com.pcbuilder.core.modules.build.dto.BuildFilterRequestDto;
import com.pcbuilder.core.modules.build.dto.BuildResponseDto;
import com.pcbuilder.core.modules.build.mapper.BuildMapper;
import com.pcbuilder.core.modules.build.model.Build;
import com.pcbuilder.core.modules.build.model.BuildItem;
import com.pcbuilder.core.modules.build.repository.BuildRepository;
import com.pcbuilder.core.modules.build.specification.BuildSpecification;
import com.pcbuilder.core.modules.components.model.Component;
import com.pcbuilder.core.modules.components.service.ComponentProvider;
import com.pcbuilder.core.modules.user.model.UserEntity;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import com.pcbuilder.core.modules.user.service.UserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuildService {

    private final BuildRepository buildRepository;
    private final BuildValidationService buildValidationService;
    private final ComponentProvider componentProvider;
    private final BuildMapper buildMapper;
    private final UserRepository userRepository;
    private final UserProvider userProvider;

    @Transactional
    public BuildResponseDto createBuild(String name, boolean isPrivate, UserPrincipal userPrincipal) {
        UserEntity user = userRepository.getReferenceById(userPrincipal.getId());
        Build build = new Build();
        build.setName(name);
        build.setUser(user);
        build.setPrivate(isPrivate);
        return buildMapper.toDto(buildRepository.save(build));
    }

    @Transactional
    public Optional<BuildResponseDto> addComponent(Long buildId, Long componentId, UserPrincipal user) {
        return getBuildIfOwner(buildId, user).map(build -> {
            List<Long> currentIds = build.getItems().stream()
                    .map(BuildItem::getComponentId).toList();
            buildValidationService.validateOrThrow(componentId, currentIds);

            Component component = componentProvider.getComponentById(componentId);

            BuildItem item = new BuildItem();
            item.setBuild(build);
            item.setComponent(component);
            item.setPriceSnapshot(component.getPrice());

            build.getItems().add(item);

            recalculateTotal(build);
            return buildMapper.toDto(buildRepository.save(build));
        });
    }

    @Transactional
    public Optional<BuildResponseDto> removeComponent(Long buildId, Long componentId, UserPrincipal user) {
        return getBuildIfOwner(buildId, user).map(build -> {
            Optional<BuildItem> itemToRemove = build.getItems().stream()
                    .filter(item -> item.getComponentId().equals(componentId))
                    .findFirst();

            itemToRemove.ifPresent(item -> {
                build.getItems().remove(item);
                recalculateTotal(build);
            });

            return buildMapper.toDto(buildRepository.save(build));
        });
    }

    @Transactional
    public Optional<BuildResponseDto> replaceComponent(Long buildId, Long oldComponentId, Long newComponentId, UserPrincipal user) {
        return getBuildIfOwner(buildId, user).map(build -> {
            List<Long> currentIds = build.getItems().stream()
                    .map(BuildItem::getComponentId)
                    .filter(id -> !id.equals(oldComponentId))
                    .toList();

            buildValidationService.validateOrThrow(newComponentId, currentIds);

            BuildItem item = build.getItems().stream()
                    .filter(i -> i.getComponentId().equals(oldComponentId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Old component not found"));

            Component newComponent = componentProvider.getComponentById(newComponentId);
            item.setComponent(newComponent);
            item.setPriceSnapshot(newComponent.getPrice());

            recalculateTotal(build);
            return buildMapper.toDto(buildRepository.save(build));
        });
    }

    public Optional<BuildResponseDto> getBuild(Long buildId, UserPrincipal currentUser) {
        return buildRepository.findById(buildId)
                .filter(build -> canView(build, currentUser))
                .map(buildMapper::toDto);
    }

    public List<BuildResponseDto> getUserBuilds(UserPrincipal currentUser, String targetUsername) {
        Long requesterId = (currentUser != null) ? currentUser.getId() : null;

        Long targetUserId;

        if(targetUsername != null) {
            targetUserId = userProvider.getUserIdByUsername(targetUsername)
                    .orElse(-1L);
        } else {
            if(requesterId == null) return List.of();
            targetUserId = requesterId;
        }

        return buildRepository.findAllVisibleBuilds(targetUserId, requesterId)
                .stream()
                .map(buildMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<BuildResponseDto> searchBuilds(
            BuildFilterRequestDto filter,
            Pageable pageable,
            UserPrincipal currentUser) {

        Long filterUserId = null;
        if(filter.getUsername() != null) {
            filterUserId = userProvider.getUserIdByUsername(filter.getUsername())
                    .orElse(-1L);
        }

        Specification<Build> spec = Specification.where(BuildSpecification.nameContains(filter.getSearchQuery()))
                .and(BuildSpecification.priceBetween(filter.getMinPrice(), filter.getMaxPrice()))
                .and(BuildSpecification.hasUserId(filterUserId));

        Long currentUserId = (currentUser != null) ? currentUser.getId() : null;
        Specification<Build> visibilitySpec = BuildSpecification.isPublicOrOwner(currentUserId);

        spec = spec.and(visibilitySpec);

        return buildRepository.findAll(spec, pageable).map(buildMapper::toDto);

    }

    private void recalculateTotal(Build build) {
        BigDecimal total = build.getItems().stream()
                .map(BuildItem::getPriceSnapshot)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        build.setTotalPrice(total);
    }

    private Optional<Build> getBuildIfOwner(Long buildId, UserPrincipal user) {
        return buildRepository.findById(buildId)
                .filter(build -> build.getUser().getId().equals(user.getId()));
    }

    private boolean canView(Build build, UserPrincipal user) {
        if (!build.isPrivate()) {
            return true;
        }

        return user != null && build.getUser().getId().equals(user.getId());
    }
}
