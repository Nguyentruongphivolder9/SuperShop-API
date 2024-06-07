package com.project.supershop.account.services.impl;


import com.project.supershop.account.domain.entities.Account;
import com.project.supershop.account.repositories.AccountRepositories;
import com.project.supershop.account.services.AccountService;
import com.project.supershop.common.ResultResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepositories accountRepositories;

    public AccountServiceImpl (AccountRepositories accountRepositories){
        this.accountRepositories = accountRepositories;
    }
    @Override
    public Iterable<Account> GetAllAccounts(){
        return (Iterable<Account>) accountRepositories.findAll();
    }
}
