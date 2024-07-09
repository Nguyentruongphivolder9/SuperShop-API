package com.project.supershop.features.account.repositories;

import com.project.supershop.features.account.domain.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AccountRepositories extends JpaRepository<Account, UUID> {
    Optional<Account> findAccountByEmail(String email);
    Account findAccountById(UUID id);
    boolean existsByEmail(String email);

    Optional<Account> findAccountByPhoneNumber(String phoneNumber);
}
