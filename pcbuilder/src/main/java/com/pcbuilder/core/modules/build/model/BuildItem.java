package com.pcbuilder.core.modules.build.model;

import com.pcbuilder.core.modules.components.model.Component;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "build_items")
@Data
public class BuildItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "build_id", nullable = false)
    private Build build;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    @Column(nullable = false)
    private BigDecimal priceSnapshot;

    public Long getComponentId() {
        return component != null ? component.getId() : null;
    }
}
