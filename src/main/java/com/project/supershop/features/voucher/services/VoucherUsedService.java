package com.project.supershop.features.voucher.services;

import com.project.supershop.features.voucher.domain.dto.responses.VoucherUsedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherUsedService {
    Page<VoucherUsedResponse> getVouchersUsed(Pageable pageable, String jwtToken);
}
