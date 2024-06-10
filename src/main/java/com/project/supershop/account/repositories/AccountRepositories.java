package com.project.supershop.account.repositories;

import com.project.supershop.account.domain.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AccountRepositories extends JpaRepository<Account, Integer> {
    Optional<Account> findAccountByEmail(String email);
}
