package com.project.supershop.features.account.domain.entities;

import com.project.supershop.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder

public class Address extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    private String addressType;
    private String location;
    private String fullName;
    private String phoneNumber;
    private boolean isDefault;
    private boolean pickupLocation;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.setCreatedAt(now);
        this.setUpdatedAt(now);
    }
}
