package com.project.supershop.features.voucher.domain.entities;

import com.project.supershop.common.BaseEntity;
import com.project.supershop.features.account.domain.entities.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "voucherUsed")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class VoucherUsed extends BaseEntity {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "voucherId")
    private Voucher voucher;

    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "accountId")
    private Account account;
}
