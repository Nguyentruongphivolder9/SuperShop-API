package com.project.supershop.account.controller;

import com.project.supershop.account.domain.entities.Account;
import com.project.supershop.account.services.AccountService;
import com.project.supershop.common.ResultResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResultResponse<List<Account>> getAllAccounts() {
        return new ResultResponse<>(accountService.getAllAccounts(), "Get all accounts successfully", 200);
    }


}
