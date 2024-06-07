package com.project.supershop.account.services;

import com.project.supershop.account.domain.entities.Account;
import com.project.supershop.common.ResultResponse;

public interface AccountService {
    Iterable<Account> GetAllAccounts();

}
