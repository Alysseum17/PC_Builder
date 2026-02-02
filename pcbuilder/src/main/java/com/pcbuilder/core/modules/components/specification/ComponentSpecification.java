package com.pcbuilder.core.modules.components.specification;

import com.pcbuilder.core.modules.components.model.Attribute;
import com.pcbuilder.core.modules.components.model.Component;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ComponentSpecification {
    public static Specification<Component> hasCategory(String categorySlug) {
        return (root, query, criteriaBuilder) -> {
            if (categorySlug == null || categorySlug.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("slug"), categorySlug);
        };
    }
    public static Specification<Component> priceBetween(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            } else if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
        };
    }
    public static Specification<Component> nameContains(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }
    public static Specification<Component> hasAttribute(String attributeName, String attributeValue) {
        return (root, query, criteriaBuilder) ->{
            if (attributeName == null || attributeValue == null ||
                attributeName.isEmpty() || attributeValue.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Component, Attribute> attributeJoin = root.join("attributes");
            query.distinct(true);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(attributeJoin.get("name"), attributeName),
                    criteriaBuilder.equal(attributeJoin.get("value"), attributeValue)
            );
        };
    }
}
