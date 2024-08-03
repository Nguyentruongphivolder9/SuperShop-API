package com.project.supershop.features.voucher.services.impl;

import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.auth.services.AccessTokenService;
import com.project.supershop.features.voucher.domain.dto.responses.DepotVoucherResponse;
import com.project.supershop.features.voucher.domain.dto.responses.VoucherUsedResponse;
import com.project.supershop.features.voucher.domain.entities.DepotVoucher;
import com.project.supershop.features.voucher.domain.entities.VoucherUsed;
import com.project.supershop.features.voucher.repositories.VoucherUsedRepository;
import com.project.supershop.features.voucher.services.VoucherUsedService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class VoucherUsedServiceImpl implements VoucherUsedService {

    private final AccessTokenService accessTokenService;
    private final ModelMapper modelMapper;
    private final VoucherUsedRepository voucherUsedRepository;
    public VoucherUsedServiceImpl(AccessTokenService accessTokenService, ModelMapper modelMapper, VoucherUsedRepository voucherUsedRepository) {
        this.accessTokenService = accessTokenService;
        this.modelMapper = modelMapper;
        this.voucherUsedRepository = voucherUsedRepository;
    }

    @Override
    public Page<VoucherUsedResponse> getVouchersUsed(Pageable pageable, String jwtToken) {
        Account existingAccount = accessTokenService.parseJwtTokenToAccount(jwtToken);
        Page<VoucherUsed> listVoucherUsed = voucherUsedRepository.findAllByAccountId(pageable, existingAccount.getId());
        return listVoucherUsed.map(voucherUsed -> {
            modelMapper.typeMap(VoucherUsed.class, VoucherUsedResponse.class).addMappings(mapper -> {
                mapper.<String>map(src -> src.getVoucher().getAccount().getId(), (dest,v) -> dest.getVoucher().setShopId(v));
                mapper.map(VoucherUsed::getVoucher, VoucherUsedResponse::setVoucher);; // map ShopId for information
            });
            return modelMapper.map(voucherUsed, VoucherUsedResponse.class);
        });
    }
}
