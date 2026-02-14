package com.pcbuilder.core.modules.build.model;

import com.pcbuilder.core.modules.user.model.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "builds")
@Data
public class Build {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean isPrivate = false;

    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuildItem> items = new ArrayList<>();
}