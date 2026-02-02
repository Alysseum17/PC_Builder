package com.pcbuilder.core.modules.components.model;


import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "attributes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "value", "unit"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

    @Column
    private String unit;

    @ManyToMany(mappedBy = "attributes")
    private Set<Component> components = new HashSet<>();

    @Override
    public String toString() {
        return name + ": " + value + (unit != null ? " " + unit : "");
    }
}
