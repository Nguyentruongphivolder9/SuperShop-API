package com.project.supershop.features.voucher.controllers;

import com.project.supershop.common.ResultResponse;
import com.project.supershop.features.voucher.domain.dto.responses.VoucherResponse;
import com.project.supershop.features.voucher.domain.dto.responses.VoucherUsedResponse;
import com.project.supershop.features.voucher.services.VoucherUsedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
public class VoucherUsedController {
    private final VoucherUsedService voucherUsedService;

    public VoucherUsedController(VoucherUsedService voucherUsedService) {
        this.voucherUsedService = voucherUsedService;
    }

    @GetMapping("/vouchers/used")
    public ResponseEntity<ResultResponse<Page<VoucherUsedResponse>>> getVouchersUsed (Pageable pageable, @RequestHeader(name = HttpHeaders.AUTHORIZATION,
                                                                                            required = false) String jwtToken) {
        Page<VoucherUsedResponse> listVoucherUsed = voucherUsedService.getVouchersUsed(pageable, jwtToken);
        ResultResponse<Page<VoucherUsedResponse>> response = ResultResponse.<Page<VoucherUsedResponse>>builder()
                .body(listVoucherUsed)
                .timeStamp(LocalDateTime.now().toString())
                .message("Get List successfully")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();

        return ResponseEntity.ok(response);
    }
}
