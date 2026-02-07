package com.pcbuilder.core.modules.build.service;

import com.pcbuilder.core.modules.build.model.CompatibilityRule;
import com.pcbuilder.core.modules.components.model.Attribute;
import com.pcbuilder.core.modules.components.model.Component;
import com.pcbuilder.core.modules.components.service.ComponentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildValidationService {
    private final ComponentProvider componentProvider;

    public void validateOrThrow(Long newComponentId, List<Long> currentIds) {
        List<String> errors = new ArrayList<>();

        Component newComp = componentProvider.getComponentById(newComponentId);
        List<Component> currentComps = componentProvider.getComponentsByIds(currentIds);

        for(Component current : currentComps) {
            checkCompatibility(newComp, current, errors);
        }
        if(!errors.isEmpty()) {
            throw new IllegalArgumentException("Compatibility issues found:\n" + String.join("\n", errors));
        }
    }

    private void checkCompatibility(Component c1, Component c2, List<String> errors) {
        String cat1 = c1.getCategory() != null ? c1.getCategory().getSlug() : "unknown";
        String cat2 = c2.getCategory() != null ? c2.getCategory().getSlug() : "unknown";

        for(CompatibilityRule rule : CompatibilityRule.values()) {
            boolean matchDirect = rule.getCategory1().equals(cat1) && rule.getCategory2().equals(cat2);
            boolean matchReverse = rule.getCategory1().equals(cat2) && rule.getCategory2().equals(cat1);
            if(matchDirect) {
                runCheck(c1, c2, rule.getAttr1(), rule.getAttr2(), rule.getType(), errors);
            } else if (matchReverse) {
                runCheck(c2, c1, rule.getAttr1(), rule.getAttr2(), rule.getType(), errors);
            }
        }
    }

    private void runCheck(Component c1, Component c2, String attr1, String attr2, CompatibilityRule.RuleType type, List<String> errors) {
        String val1 = getAttributeValue(c1, attr1);
        String val2 = getAttributeValue(c2, attr2);
        if(val1 == null || val2 == null) return;

        boolean passed = true;

        try {
            switch (type) {
                case EQUALS -> passed = val1.equalsIgnoreCase(val2);
                case LTE -> passed = parseNum(val1) <= parseNum(val2);
                case GTE -> passed = parseNum(val1) >= parseNum(val2);
            }
        } catch (Exception e) {
            return;
        }
        if(!passed) {
            errors.add(String.format("Conflict: %s (%s) vs %s (%s)",
                    c1.getName(), val1, c2.getName(), val2));
        }
    }

    private double parseNum(String val) {
        return Double.parseDouble(val.replaceAll("[^0-9.]", ""));
    }
    private String getAttributeValue(Component comp, String attrName) {
        return comp.getAttributes().stream()
                .filter(a -> a.getName().equalsIgnoreCase(attrName))
                .findFirst()
                .map(Attribute::getValue)
                .orElse(null);

    }
}
