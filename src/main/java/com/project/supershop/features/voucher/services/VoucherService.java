package com.project.supershop.features.voucher.services;

import com.project.supershop.features.voucher.domain.dto.requests.VoucherRequest;
import com.project.supershop.features.voucher.domain.dto.responses.VoucherResponse;
import com.project.supershop.features.voucher.domain.entities.Voucher;

import java.util.Optional;


public interface VoucherService {
    VoucherResponse createVoucher(VoucherRequest voucherRequest);

}
