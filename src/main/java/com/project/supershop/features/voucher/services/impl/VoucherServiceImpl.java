package com.project.supershop.features.voucher.services.impl;

import com.project.supershop.features.voucher.domain.dto.requests.VoucherRequest;
import com.project.supershop.features.voucher.domain.dto.responses.VoucherResponse;
import com.project.supershop.features.voucher.domain.entities.Voucher;
import com.project.supershop.features.voucher.repositories.VoucherRepository;
import com.project.supershop.features.voucher.services.VoucherService;
import com.project.supershop.features.voucher.utils.enums.StatusVoucherEnum;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class VoucherServiceImpl implements VoucherService {

    private final ModelMapper modelMapper;
    private final VoucherRepository voucherRepository;

    public VoucherServiceImpl(ModelMapper modelMapper, VoucherRepository voucherRepository) {
        this.modelMapper = modelMapper;
        this.voucherRepository = voucherRepository;
    }

    @Override
    public VoucherResponse createVoucher(VoucherRequest voucherRequest) {
        Voucher voucher = Voucher.createVoucher(voucherRequest);

        Optional<Voucher> voucherByCode = voucherRepository.findByCode(voucherRequest.getCode());
        if(voucherByCode.isPresent()){
            Voucher existingVoucher = voucherByCode.get();
            if(!existingVoucher.getStatus().equals(StatusVoucherEnum.EXPIRE.getValue())){
                throw new RuntimeException("error");
            }
        }

        Voucher voucherSaving = voucherRepository.save(voucher);
        VoucherResponse voucherResponse = modelMapper.map(voucherSaving, VoucherResponse.class);
        return voucherResponse;
    }


}
