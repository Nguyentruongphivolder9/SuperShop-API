package com.project.supershop.features.account.controller;

import com.project.supershop.features.account.domain.dto.request.LogoutRequest;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.common.ResultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get-all")
    public ResponseEntity<ResultResponse<List<Account>>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();

        return ResponseEntity.ok(
                ResultResponse.<List<Account>>builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .body(accounts)
                        .message("Received all accounts successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<ResultResponse> accountLogout(@RequestHeader("Authorization") String authorizationHeader, @RequestBody LogoutRequest logoutRequest) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        String token = authorizationHeader.substring(7);

        accountService.logoutAccount(logoutRequest.getEmail(), token);
        return ResponseEntity.ok(
                ResultResponse.<List<Account>>builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .body(null)
                        .message("Account with email: " + logoutRequest.getEmail() + " logged out successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }


}
