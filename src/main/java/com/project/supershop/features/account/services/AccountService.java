package com.project.supershop.features.account.services;

import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.auth.dto.request.RegisterRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface AccountService {
    List<Account> getAllAccounts();
    Account saveAccount(RegisterRequest registerRequest);
    Account convertToAccount(UserDetails userDetails);

    //Finding interfaces
    Account findByEmail(String email);
    Account findByPhoneNumber(String phoneNumber);
    //Email verify token interfaces
    Boolean verifyToken(String token);

}
