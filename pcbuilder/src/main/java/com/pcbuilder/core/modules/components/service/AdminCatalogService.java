package com.pcbuilder.core.modules.components.service;

import com.pcbuilder.core.modules.auth.dto.MessageResponse;
import com.pcbuilder.core.modules.components.dto.AttributeDto;
import com.pcbuilder.core.modules.components.dto.ComponentRequestDto;
import com.pcbuilder.core.modules.components.mapper.ComponentMapper;
import com.pcbuilder.core.modules.components.model.Attribute;
import com.pcbuilder.core.modules.components.model.Component;
import com.pcbuilder.core.modules.components.repository.AttributeRepository;
import com.pcbuilder.core.modules.components.repository.CategoryRepository;
import com.pcbuilder.core.modules.components.repository.ComponentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminCatalogService {

    private final ComponentRepository componentRepository;
    private final CategoryRepository categoryRepository;
    private final AttributeRepository attributeRepository;
    private final ComponentMapper componentMapper;

    @Transactional
    public Optional<MessageResponse> saveComponent(ComponentRequestDto request) {

       return categoryRepository.findById(request.getCategoryId()).map(category -> {
           Component component = new Component();
           component.setName(request.getName());
           component.setPrice(request.getPrice());
           component.setImageUrl(request.getImageUrl());
           component.setCategory(category);

           String generatedSlug = generateUniqueSlug(request.getName());
           component.setSlug(generatedSlug);

           if (request.getAttributes() != null) {
               Set<Attribute> attributes = new HashSet<>();

               for (AttributeDto attrDto : request.getAttributes()) {
                   Attribute attribute = attributeRepository.findByNameAndValueAndUnit(
                           attrDto.getName(), attrDto.getValue(), attrDto.getUnit()
                   ).orElseGet(() -> {
                       Attribute newAttr = new Attribute();
                       newAttr.setName(attrDto.getName());
                       newAttr.setValue(attrDto.getValue());
                       newAttr.setUnit(attrDto.getUnit());
                       return attributeRepository.save(newAttr);
                   });
                   attributes.add(attribute);
               }
               component.setAttributes(attributes);
           }

           componentRepository.save(component);

           return new MessageResponse("Component saved successfully with slug: " + generatedSlug);
        });

    }

    @Transactional
    public void updatePricesByCategory(String categorySlug, double percent) {

        BigDecimal factor = BigDecimal.valueOf(1 + (percent / 100.0));

        int updatedCount = componentRepository.updatePricesByCategory(categorySlug, factor);

        if (updatedCount == 0) {
            throw new EntityNotFoundException("There are no components found for category slug: " + categorySlug);
        }
    }

    private String generateUniqueSlug(String productName) {
        String baseSlug = productName.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");

        List<String> similarSlugs = componentRepository.findSimilarSlugs(baseSlug + "%");

        if (similarSlugs.isEmpty()) {
            return baseSlug;
        }

        if (!similarSlugs.contains(baseSlug)) {
            return baseSlug;
        }

        int maxCounter = similarSlugs.stream()
                .filter(slug -> slug.matches(baseSlug + "-\\d+")) // Відфільтрувати "base-pro", залишити тільки "base-123"
                .map(slug -> {
                    String numberPart = slug.substring(baseSlug.length() + 1);
                    return Integer.parseInt(numberPart);
                })
                .max(Integer::compareTo)
                .orElse(0);

        return baseSlug + "-" + (maxCounter + 1);
    }
}
