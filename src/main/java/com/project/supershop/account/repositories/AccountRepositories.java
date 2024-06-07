package com.project.supershop.account.repositories;

import com.project.supershop.account.domain.entities.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepositories extends CrudRepository<Account, Integer> {
}
