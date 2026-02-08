package com.pcbuilder.core.modules.build.controller;

import com.pcbuilder.core.modules.auth.userdetails.UserPrincipal;
import com.pcbuilder.core.modules.build.dto.*;
import com.pcbuilder.core.modules.build.service.BuildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/builds")
@RequiredArgsConstructor
public class BuildController {

    private final BuildService buildService;

    // 1. Створити нову збірку
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BuildResponseDto> createBuild(
            @RequestBody @Valid CreateBuildRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(
                buildService.createBuild(request.getName(), request.isPrivate(), user)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildResponseDto> getBuild(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return buildService.getBuild(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BuildResponseDto>> searchBuilds(
            @ModelAttribute BuildFilterRequestDto filter,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 20) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(
                buildService.searchBuilds(filter, pageable, currentUser)
        );
    }

    @GetMapping("/user")
    public ResponseEntity<List<BuildResponseDto>> getUserBuilds(
            @RequestParam(required = false) String username,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(
                buildService.getUserBuilds(currentUser, username)
        );
    }

    @PostMapping("/{buildId}/components")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BuildResponseDto> addComponent(
            @PathVariable Long buildId,
            @RequestBody @Valid AddComponentRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return buildService.addComponent(buildId, request.getComponentId(), user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{buildId}/components/{componentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BuildResponseDto> removeComponent(
            @PathVariable Long buildId,
            @PathVariable Long componentId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return buildService.removeComponent(buildId, componentId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{buildId}/components/{oldComponentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BuildResponseDto> replaceComponent(
            @PathVariable Long buildId,
            @PathVariable Long oldComponentId,
            @RequestBody @Valid ReplaceComponentRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return buildService.replaceComponent(buildId, oldComponentId, request.getNewComponentId(), user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}