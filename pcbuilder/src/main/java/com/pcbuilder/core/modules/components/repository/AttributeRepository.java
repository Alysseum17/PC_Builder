package com.pcbuilder.core.modules.components.repository;

import com.pcbuilder.core.modules.components.model.Attribute;
import com.pcbuilder.core.modules.components.repository.projection.FilterOptionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    @Query("""
    SELECT 
        attr.name as name, 
        attr.value as value, 
        COUNT(c.id) as count 
    FROM Component c 
    JOIN c.attributes attr 
    JOIN c.category cat 
    WHERE cat.slug = :categorySlug 
    GROUP BY attr.name, attr.value
    ORDER BY attr.name, attr.value
""")
    List<FilterOptionProjection> findDistinctComponentCategory(@Param("categorySlug") String categorySlug);

    Optional<Attribute> findByNameAndValueAndUnit(String name, String value, String unit);
}
