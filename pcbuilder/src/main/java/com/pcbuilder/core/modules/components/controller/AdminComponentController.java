package com.pcbuilder.core.modules.components.controller;

import com.pcbuilder.core.modules.auth.dto.MessageResponse;
import com.pcbuilder.core.modules.components.dto.ComponentRequestDto;
import com.pcbuilder.core.modules.components.service.AdminCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/components")
@RequiredArgsConstructor
public class AdminComponentController {

    private final AdminCatalogService adminCatalogService;

    @PostMapping
    public ResponseEntity<MessageResponse> createComponent(@RequestBody @Valid ComponentRequestDto request) {
        return adminCatalogService.saveComponent(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/categories/{categorySlug}/prices")
    public ResponseEntity<MessageResponse> updatePrices(
            @PathVariable String categorySlug,
            @RequestParam double percent
    ) {
        adminCatalogService.updatePricesByCategory(categorySlug, percent);
        return ResponseEntity.ok(new MessageResponse("Prices updated successfully for category: " + categorySlug));
    }
}
