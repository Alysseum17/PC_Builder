package com.pcbuilder.core.modules.build.model;

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

    @Column(nullable = false)
    private Long componentId;

    @Column(nullable = false)
    private BigDecimal priceSnapshot;
}
