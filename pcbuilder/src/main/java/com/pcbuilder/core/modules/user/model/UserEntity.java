package com.pcbuilder.core.modules.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider = AuthProvider.LOCAL;
    private String providerId;

    private boolean twoFactorEnabled = false;
    private String twoFactorSecret;
    @ElementCollection
    @CollectionTable(name = "user_backup_codes",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "backup_code")
    private Set<String> backupCodes = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }

    public String getProvider() {
        return authProvider.name();
    }

}
