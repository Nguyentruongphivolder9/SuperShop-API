package com.project.supershop.features.email.domain.entities;

import com.project.supershop.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "confirmations")
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class Confirmation extends BaseEntity {
    private String token;
    private String email;

    public Confirmation(String email) {
        this.email = email;
        this.token = UUID.randomUUID().toString();
    }

    @PrePersist
    protected void onCreate() {
        this.setCreatedAt(LocalDateTime.now());
    }
}
