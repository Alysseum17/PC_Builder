package com.pcbuilder.core.modules.components.service;

import com.pcbuilder.core.modules.components.dto.ComponentFilterRequestDto;
import com.pcbuilder.core.modules.components.dto.ComponentResponseDto;
import com.pcbuilder.core.modules.components.mapper.ComponentMapper;
import com.pcbuilder.core.modules.components.model.Component;
import com.pcbuilder.core.modules.components.repository.AttributeRepository;
import com.pcbuilder.core.modules.components.repository.ComponentRepository;
import com.pcbuilder.core.modules.components.repository.projection.FilterOptionProjection;
import com.pcbuilder.core.modules.components.specification.ComponentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final ComponentRepository componentRepository;
    private final AttributeRepository attributeRepository;
    private final ComponentMapper componentMapper;

    public Page<ComponentResponseDto> searchComponents(ComponentFilterRequestDto filters, Pageable pageable) {
        Specification<Component> specification = Specification.where((Specification<Component>)null);
        if(filters.getCategory() != null) {
            specification = specification.and(
                    ComponentSpecification.hasCategory(filters.getCategory())
            );
        }
        if(filters.getMinPrice() != null || filters.getMaxPrice() != null) {
            specification = specification.and(
                    ComponentSpecification.priceBetween(filters.getMinPrice(), filters.getMaxPrice())
            );
        }
        if(filters.getSearchQuery() != null) {
            specification = specification.and(
                    ComponentSpecification.nameContains(filters.getSearchQuery())
            );
        }
        if(filters.getAttributes() != null) {
            for(Map.Entry<String, String> entry : filters.getAttributes().entrySet()) {
                specification = specification.and(
                        ComponentSpecification.hasAttribute(entry.getKey(), entry.getValue())
                );
            }
        }
        Page<Component> componentsPage = componentRepository.findAll(specification, pageable);
        return componentsPage.map(componentMapper::toDto);
    }

    public Map<String, List<String>> getAttributesByCategory(String categorySlug) {
        List<FilterOptionProjection> attributes = attributeRepository.findDistinctComponentCategory(categorySlug);

        return attributes.stream()
                .collect(Collectors.groupingBy(
                        FilterOptionProjection::getName,
                        Collectors.mapping(FilterOptionProjection::getValue, Collectors.toList())
                ));
    }

    public Optional<ComponentResponseDto> getComponentBySlug(String slug) {
        return componentRepository.findBySlug(slug)
                .map(componentMapper::toDto);

    }

}
