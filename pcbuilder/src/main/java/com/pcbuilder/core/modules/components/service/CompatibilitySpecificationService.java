package com.pcbuilder.core.modules.components.service;

import com.pcbuilder.core.modules.build.model.Build;
import com.pcbuilder.core.modules.build.model.BuildItem;
import com.pcbuilder.core.modules.build.model.CompatibilityRule;
import com.pcbuilder.core.modules.build.service.BuildProvider;
import com.pcbuilder.core.modules.components.model.Attribute;
import com.pcbuilder.core.modules.components.model.Component;
import com.pcbuilder.core.modules.components.repository.ComponentRepository;
import com.pcbuilder.core.modules.components.specification.ComponentSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompatibilitySpecificationService {

    private final BuildProvider buildProvider;
    private final ComponentRepository componentRepository;

    public Specification<Component> createCompatibilitySpec(Long buildId, String targetCategorySlug) {
        Specification<Component> spec = Specification.where((Specification<Component>)null);

        if (buildId == null) return spec;

        Build build = buildProvider.getBuildById(buildId)
                .orElseThrow(() -> new EntityNotFoundException("Build not found"));

        if (build.getItems().isEmpty()) return spec;

        List<Component> currentComponents = build.getItems().stream()
                .map(BuildItem::getComponent)
                .toList();

        for (CompatibilityRule rule : CompatibilityRule.values()) {

            String otherCategorySlug = null;
            String targetAttr = null;
            String otherAttr = null;

            if (rule.getCategory1().equals(targetCategorySlug)) {
                otherCategorySlug = rule.getCategory2();
                targetAttr = rule.getAttr1();
                otherAttr = rule.getAttr2();
            } else if (rule.getCategory2().equals(targetCategorySlug)) {
                otherCategorySlug = rule.getCategory1();
                targetAttr = rule.getAttr2();
                otherAttr = rule.getAttr1();
            }

            if (otherCategorySlug == null) continue;

            String finalOtherCategorySlug = otherCategorySlug;
            Component existingComponent = currentComponents.stream()
                    .filter(c -> c.getCategory().getSlug().equals(finalOtherCategorySlug))
                    .findFirst()
                    .orElse(null);

            if (existingComponent != null) {

                String requiredValue = getAttributeValue(existingComponent, otherAttr);

                if (requiredValue != null) {
                    switch (rule.getType()) {
                        case EQUALS -> spec = spec.and(ComponentSpecification.hasAttribute(targetAttr, requiredValue));
                        case LTE -> spec = spec.and(ComponentSpecification.attributeLte(targetAttr, requiredValue));
                        case GTE -> spec = spec.and(ComponentSpecification.attributeGte(targetAttr, requiredValue));
                    }
                }
            }
        }

        return spec;
    }

    private String getAttributeValue(Component c, String name) {
        return c.getAttributes().stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(Attribute::getValue)
                .orElse(null);
    }
}
