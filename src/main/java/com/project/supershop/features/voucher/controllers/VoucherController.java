package com.project.supershop.features.voucher.controllers;

import com.project.supershop.common.ResultResponse;
import com.project.supershop.features.voucher.domain.dto.requests.VoucherRequest;
import com.project.supershop.features.voucher.domain.dto.responses.VoucherResponse;
import com.project.supershop.features.voucher.services.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/vouchers")
public class VoucherController {
    private final VoucherService voucherService;


    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @PostMapping
    public ResponseEntity<ResultResponse<VoucherResponse>> createVoucher(@RequestBody VoucherRequest voucherRequest){
        VoucherResponse voucherResponse = voucherService.createVoucher(voucherRequest);
        ResultResponse<VoucherResponse> response = ResultResponse.<VoucherResponse>builder()
                .body(voucherResponse)
                .timeStamp(LocalDateTime.now().toString())
                .message("Create voucher successfully")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
