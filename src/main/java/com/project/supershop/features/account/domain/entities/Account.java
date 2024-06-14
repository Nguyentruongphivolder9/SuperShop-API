package com.project.supershop.features.account.domain.entities;
import com.project.supershop.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class Account extends BaseEntity {
    private String userName;
    private String password;
    private String avatarUrl;
    @Pattern(regexp = "ADMIN|USER|SELLER")
    private String roleName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDateTime birthDay;
    private String gender;
    private Boolean isActive;
    private Boolean isEnable;

}
