package com.project.supershop.account.services;

import com.project.supershop.account.domain.dto.response.AccountResponse;
import com.project.supershop.account.domain.entities.Account;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface AccountService {
    List<Account> getAllAccounts();
//    Account finByName(String name);
    Account findByEmail(String email);
    Account convertToAccount(UserDetails userDetails);
}
