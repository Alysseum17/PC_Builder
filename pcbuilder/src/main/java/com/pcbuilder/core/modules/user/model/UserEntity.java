package com.pcbuilder.core.modules.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String hash_password;
    @Column(nullable = false, unique = true)
    private String email;
    private String avatar_url;
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    @Enumerated(EnumType.STRING)
    private HashSet<UserRole> roles;
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }

}
