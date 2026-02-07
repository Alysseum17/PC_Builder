package com.pcbuilder.core.modules.build.specification;

import com.pcbuilder.core.modules.build.model.Build;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class BuildSpecification {

    public static Specification<Build> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Build> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return cb.conjunction();
            
            if (min != null && max != null) {
                return cb.between(root.get("totalPrice"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("totalPrice"), min);
            } else {
                return cb.lessThanOrEqualTo(root.get("totalPrice"), max);
            }
        };
    }

    public static Specification<Build> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return cb.conjunction();
            return cb.equal(root.get("userId"), userId);
        };
    }

    public static Specification<Build> isPublic() {
        return (root, query, cb) -> cb.isFalse(root.get("isPrivate"));
    }

    public static Specification<Build> isOwner(Long currentUserId) {
        return (root, query, cb) -> {
            if (currentUserId == null) return cb.disjunction();
            return cb.equal(root.get("userId"), currentUserId);
        };
    }

    public static Specification<Build> isPublicOrOwner(Long currentUserId) {
        return (root, query, cb) -> {
            Specification<Build> publicSpec = isPublic();
            Specification<Build> ownerSpec = isOwner(currentUserId);

            return cb.or(
                publicSpec.toPredicate(root, query, cb),
                ownerSpec.toPredicate(root, query, cb)
            );
        };
    }
}