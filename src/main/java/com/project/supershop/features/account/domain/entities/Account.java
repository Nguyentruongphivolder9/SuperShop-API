package com.project.supershop.features.account.domain.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.supershop.common.BaseEntity;
import com.project.supershop.features.account.utils.enums.Provider;
import com.project.supershop.features.voucher.domain.entities.Voucher;
import com.project.supershop.features.voucher.domain.entities.VoucherUsed;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    private String device;
    private Boolean isActive;
    private Boolean isLoggedOut;
    private Boolean isEnable;
    //Phương thức user đã dùng để đăng nhâp
    private String provider;

    public Account(String roles, String name, String email, String id){
        this.roleName = roles;
        this.userName = name;
        this.email = email;
        this.setId(UUID.fromString(id));
    }


    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Address> addresses;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Voucher> vouchers;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<VoucherUsed> vouchersUsed;
}
