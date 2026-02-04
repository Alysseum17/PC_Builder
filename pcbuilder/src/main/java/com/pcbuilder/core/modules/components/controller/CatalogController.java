package com.pcbuilder.core.modules.components.controller;

import com.pcbuilder.core.modules.components.dto.ComponentFilterRequestDto;
import com.pcbuilder.core.modules.components.dto.ComponentResponseDto;
import com.pcbuilder.core.modules.components.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/search")
    public ResponseEntity<Page<ComponentResponseDto>> searchComponents(
            @ModelAttribute ComponentFilterRequestDto filterDto,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(catalogService.searchComponents(filterDto, pageable));
    }

    @GetMapping("/filters/{categorySlug}")
    public ResponseEntity<Map<String, List<String>>> getFilters(@PathVariable String categorySlug) {
        return ResponseEntity.ok(catalogService.getAttributesByCategory(categorySlug));
    }

    @GetMapping("/products/{slug}")
    public ResponseEntity<ComponentResponseDto> getComponent(@PathVariable String slug) {
        return catalogService.getComponentBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}