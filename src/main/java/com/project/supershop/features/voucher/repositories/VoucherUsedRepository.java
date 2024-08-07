package com.project.supershop.features.voucher.repositories;

import com.project.supershop.features.voucher.domain.entities.DepotVoucher;
import com.project.supershop.features.voucher.domain.entities.VoucherUsed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoucherUsedRepository extends JpaRepository<VoucherUsed, UUID>, PagingAndSortingRepository<VoucherUsed, UUID> {
    Page<VoucherUsed> findAllByAccountId(Pageable pageable, UUID accountId);
}
