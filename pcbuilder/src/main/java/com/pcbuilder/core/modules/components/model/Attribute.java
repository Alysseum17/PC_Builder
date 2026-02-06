package com.pcbuilder.core.modules.components.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "attributes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "value", "unit"})
})
@Data
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String value;

    @Column(length = 20)
    private String unit;
}