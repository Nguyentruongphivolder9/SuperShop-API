package com.project.supershop.features.email.domain.entities;

import com.project.supershop.features.account.domain.entities.Account;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public Confirmation(Account account) {
        this.account = account;
        this.token = UUID.randomUUID().toString();
    }

    @PrePersist
    protected void onCreate() {
        this.setCreatedAt(LocalDateTime.now());
    }
}
