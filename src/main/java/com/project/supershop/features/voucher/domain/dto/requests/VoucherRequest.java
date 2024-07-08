package com.project.supershop.features.voucher.domain.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoucherRequest {
    private String name;
    private String code;
    private String voucherType;
    private String discountType;
    private Double fixedAmount;
    private Double percentageAmount;
    private Double maximumDiscount;
    private Double minimumTotalOrder;
    private Boolean isLimit;
    private Integer quantity;
    private Integer maxDistribution;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
