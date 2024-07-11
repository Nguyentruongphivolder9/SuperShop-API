package com.project.supershop.features.voucher.repositories;

import com.project.supershop.features.voucher.domain.entities.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, UUID>, PagingAndSortingRepository<Voucher, UUID> {
    Optional<Voucher> findByCode(String code);
    Optional<Voucher> findById(UUID id);
    Page<Voucher> findAll(Pageable pageable);
}
