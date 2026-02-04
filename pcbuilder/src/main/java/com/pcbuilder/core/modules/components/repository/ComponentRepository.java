package com.pcbuilder.core.modules.components.repository;

import com.pcbuilder.core.modules.components.model.Component;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long>, JpaSpecificationExecutor<Component> {
    @EntityGraph(attributePaths = {"category", "attributes"})
    Optional<Component> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Modifying
    @Query("UPDATE Component c SET c.price = c.price * :factor WHERE c.category.slug = :categorySlug")
    int updatePricesByCategory(@Param("categorySlug") String categorySlug, @Param("factor") BigDecimal factor);

    @Query("SELECT c.slug FROM Component c WHERE c.slug LIKE :pattern")
    List<String> findSimilarSlugs(@Param("pattern") String pattern);
}
