package com.project.supershop.features.auth.domain.entities;

import com.project.supershop.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "accessTokens")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class AccessToken extends BaseEntity {
    @Column(length = 1000)
    private String token;
    private String refreshToken;
    private long expiresIn;
    private long issuedAt;
    private long expiresAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.setCreatedAt(now);
        this.setUpdatedAt(now);
    }

}
