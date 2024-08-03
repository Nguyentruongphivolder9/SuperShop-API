package com.project.supershop.features.account.controller;

import com.project.supershop.features.account.domain.dto.request.AccountRequest;
import com.project.supershop.features.account.domain.dto.request.LogoutRequest;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.common.ResultResponse;
import com.project.supershop.features.auth.domain.dto.request.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
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


    @PostMapping("/account-logout")
    public ResponseEntity<ResultResponse> accountLogout(@RequestBody LogoutRequest logoutRequest, @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }

        String token = authorizationHeader.substring(7);
        try {
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResultResponse.<List<Account>>builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .body(null)
                            .message("Error logging out: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build()
            );
        }
    }


}
