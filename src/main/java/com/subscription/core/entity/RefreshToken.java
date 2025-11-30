package com.subscription.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "token", nullable = false, length = 64)
    private String token;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    public RefreshToken(String userId) {
        this.token = UUID.randomUUID().toString();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);
        this.revoked = false;
    }
}

