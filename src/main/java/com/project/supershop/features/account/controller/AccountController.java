package com.project.supershop.features.account.controller;

import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.common.ResultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<ResultResponse> getAllAccounts() {
        List<Account> account = accountService.getAllAccounts();

        return ResponseEntity.created(URI.create("")).body(
                ResultResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .body(account)
                    .message("Recive all accounts successfully")
                    .statusCode(HttpStatus.FOUND.value())
                .build()
        );
    }





}
