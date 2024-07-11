package com.project.supershop.features.voucher.services.impl;

import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.repositories.AccountRepositories;
import com.project.supershop.features.voucher.domain.dto.requests.VoucherRequest;
import com.project.supershop.features.voucher.domain.dto.responses.VoucherResponse;
import com.project.supershop.features.voucher.domain.entities.Voucher;
import com.project.supershop.features.voucher.repositories.VoucherRepository;
import com.project.supershop.features.voucher.services.VoucherService;
import com.project.supershop.features.voucher.utils.enums.DiscountTypeEnum;
import com.project.supershop.features.voucher.utils.enums.StatusVoucherEnum;
import com.project.supershop.features.voucher.utils.jwtUltis.JwtAuthUtils;
import com.project.supershop.handler.UnprocessableException;
import io.jsonwebtoken.Claims;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class VoucherServiceImpl implements VoucherService {
    private final String secretKey;
    private final ModelMapper modelMapper;
    private final JwtAuthUtils jwtAuthUtils;
    private final VoucherRepository voucherRepository;
    private final AccountRepositories accountRepositories;


    public VoucherServiceImpl(@Value("${TOKEN_SECRET_KEY}") String secretKey, ModelMapper modelMapper, JwtAuthUtils jwtAuthUtils, VoucherRepository voucherRepository, AccountRepositories accountRepositories) {
        this.secretKey = secretKey;
        this.modelMapper = modelMapper;
        this.jwtAuthUtils = jwtAuthUtils;
        this.voucherRepository = voucherRepository;
        this.accountRepositories = accountRepositories;
    }

    @Override
    public VoucherResponse createVoucher(VoucherRequest voucherRequest, String jwtToken) {
        Voucher voucher = Voucher.createVoucher(voucherRequest);
        Claims jwt = jwtAuthUtils.decodeJwt(jwtToken, secretKey);
        List<String> roles = Arrays.stream(jwt.get("role", String.class).split(",")).toList();

        Optional<Account> account = accountRepositories.findAccountByEmail((String) jwt.get("email")); // subject was being setting as email
        if(account.isPresent()){
            Account foundAccount = account.get();
            if(foundAccount.getRoleName().equalsIgnoreCase("admin")){
                voucher.setAccount(null);
            }
            voucher.setAccount(foundAccount);
        }

        Optional<Voucher> voucherByCode = voucherRepository.findByCode(voucherRequest.getCode());
        if(voucherByCode.isPresent()){
            Voucher existingVoucher = voucherByCode.get();
            if(!existingVoucher.getStatus().equals(StatusVoucherEnum.EXPIRE.value())){
                throw new UnprocessableException("Duplicate voucher code, please use another one");
            }
        }

        Voucher voucherSaving = voucherRepository.save(voucher);
        TypeMap<Voucher, VoucherResponse> voucherResponseTypeMap = modelMapper.typeMap(Voucher.class, VoucherResponse.class)
                .addMappings(mapper -> {mapper.map(src -> src.getAccount().getId(), VoucherResponse::setShopId);
        });
        return modelMapper.map(voucherSaving, VoucherResponse.class);
    }

    @Override
    public VoucherResponse getVoucherById(String id) {
        return voucherRepository.findById(UUID.fromString(id))
                .map(voucher -> {
                    modelMapper.typeMap(Voucher.class, VoucherResponse.class).addMappings(mapper -> {
                        mapper.map(src -> src.getAccount().getId(), VoucherResponse::setShopId);
                    });
                    return modelMapper.map(voucher, VoucherResponse.class);
                })
                .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + id));
    }

    @Override
    public Page<VoucherResponse> getVouchers(Pageable pageable) {

        Page<Voucher> vouchers = voucherRepository.findAll(pageable);
        return vouchers.map(voucher -> {
            modelMapper.typeMap(Voucher.class, VoucherResponse.class).addMappings(mapper -> {
                mapper.map(src -> src.getAccount().getId(), VoucherResponse::setShopId);
            });
            return modelMapper.map(voucher, VoucherResponse.class);
        });
    }

    @Override
    public VoucherResponse partialUpdate(String id, VoucherRequest voucherRequest) {

        return voucherRepository.findById(UUID.fromString(id))
                .map(existingVoucher -> {
                    String existingStatus = existingVoucher.getStatus();
                    if(existingStatus.equals(StatusVoucherEnum.ONGOING.value())){
                        if(voucherRequest.getDiscountType().equals(DiscountTypeEnum.FIXED.value()) ||
                           voucherRequest.getDiscountType().equals(DiscountTypeEnum.PERCENTAGE.value())){
                            Optional.ofNullable(voucherRequest.getName()).ifPresent(existingVoucher::setName);
                            Optional.ofNullable(voucherRequest.getQuantity()).ifPresent(existingVoucher::setQuantity);
                            Optional.ofNullable(voucherRequest.getEndDate()).ifPresent(existingVoucher::setEndDate);
                        }
                    }

                    if(existingStatus.equals(StatusVoucherEnum.UPCOMING.value())){
                        Optional.ofNullable(voucherRequest.getName()).ifPresent(existingVoucher::setName);
                        Optional.ofNullable(voucherRequest.getMinimumTotalOrder()).ifPresent(existingVoucher::setMinimumTotalOrder);
                        Optional.ofNullable(voucherRequest.getQuantity()).ifPresent(existingVoucher::setQuantity);
                        Optional.ofNullable(voucherRequest.getMaxDistribution()).ifPresent(existingVoucher::setMaxDistribution);
                        Optional.ofNullable(voucherRequest.getStartDate()).ifPresent(existingVoucher::setStartDate);
                        Optional.ofNullable(voucherRequest.getEndDate()).ifPresent(existingVoucher::setEndDate);

                        //case same discountType
                        if(existingVoucher.getDiscountType().equals(voucherRequest.getDiscountType())){
                            if(voucherRequest.getDiscountType().equals(DiscountTypeEnum.FIXED.value())){
                                Optional.ofNullable(voucherRequest.getFixedAmount()).ifPresent(existingVoucher::setFixedAmount);

                            }
                            if(existingVoucher.getDiscountType().equals(voucherRequest.getDiscountType()) &&
                                    voucherRequest.getDiscountType().equals(DiscountTypeEnum.PERCENTAGE.value())){
                                Optional.ofNullable(voucherRequest.getPercentageAmount()).ifPresent(existingVoucher::setPercentageAmount);
                                Optional.ofNullable(voucherRequest.getIsLimit()).ifPresent(existingVoucher::setIsLimit);
                                Optional.ofNullable(voucherRequest.getMaximumDiscount()).ifPresent(existingVoucher::setMaximumDiscount);
                            }
                        }

                        // case not same discountType
                        if(!existingVoucher.getDiscountType().equals(voucherRequest.getDiscountType())){
                            if(voucherRequest.getDiscountType().equals(DiscountTypeEnum.PERCENTAGE.value())){
                                existingVoucher.setFixedAmount(null);
                                Optional.of(voucherRequest.getDiscountType()).ifPresent(existingVoucher::setDiscountType);
                                Optional.ofNullable(voucherRequest.getPercentageAmount()).ifPresent(existingVoucher::setPercentageAmount);
                                Optional.ofNullable(voucherRequest.getIsLimit()).ifPresent(existingVoucher::setIsLimit);
                                Optional.ofNullable(voucherRequest.getMaximumDiscount()).ifPresent(existingVoucher::setMaximumDiscount);
                            }

                            if(voucherRequest.getDiscountType().equals(DiscountTypeEnum.FIXED.value())){
                                Optional.of(voucherRequest.getDiscountType()).ifPresent(existingVoucher::setDiscountType);
                                Optional.ofNullable(voucherRequest.getFixedAmount()).ifPresent(existingVoucher::setFixedAmount);
                                existingVoucher.setPercentageAmount(null);
                                existingVoucher.setIsLimit(null);
                                existingVoucher.setMaximumDiscount(null);
                            }
                        }

                    }
                    voucherRepository.save(existingVoucher);
                    modelMapper.typeMap(Voucher.class, VoucherResponse.class).addMappings(mapper -> {
                        mapper.map(src -> src.getAccount().getId(), VoucherResponse::setShopId);
                    });
                    return modelMapper.map(existingVoucher, VoucherResponse.class);
                })
                .orElseThrow(() -> new RuntimeException("Voucher does not exist"));
    }
}
