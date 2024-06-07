package com.project.supershop.account.controller;

import com.project.supershop.account.domain.entities.Account;
import com.project.supershop.account.services.AccountService;
import com.project.supershop.common.ResultResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private fianl JWTservice jwtSErvice;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;

    //Initilization...
    public AccountController (AccountService accountService, JWTservice jwtSErvice, AuthenticationManager authenticationManager) {
        this.JWTservice = jwtSErvice;
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;
    }

    @GetMapping
    public ResultResponse<Iterable<Account>> getAllAccounts() {
        return new ResultResponse<Iterable<Account>>(accountService.GetAllAccounts(),"Get all accounts sucessfully", 200 );
    }
}
