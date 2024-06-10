package com.project.supershop.account.controller;

import com.project.supershop.account.domain.dto.request.AuthRequest;
import com.project.supershop.account.domain.entities.Account;
import com.project.supershop.account.services.AccountService;
import com.project.supershop.auth.JwtTokenService.JwtTokenService;
import com.project.supershop.common.ResultResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/auth")
public class AccountController {

    private final JwtTokenService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;

    public AccountController(AccountService accountService, JwtTokenService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;
    }

    @GetMapping
    public ResultResponse<List<Account>> getAllAccounts() {
        return new ResultResponse<>(accountService.getAllAccounts(), "Get all accounts successfully", 200);
    }

    @PostMapping("/login")
    public ResultResponse<String> authenticateUser(@RequestBody AuthRequest authRequest) {
        // Perform authentication
        System.out.println(authRequest.getEmail() + " and " + authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        // Set authentication context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println(authentication.getPrincipal());
        // Extract user details and create JWT token
        Object principal = authentication.getPrincipal();
        System.out.println(principal.getClass());

        // Cast to Account or convert from UserDetails
        Account account;
        if (principal instanceof Account) {
            account = (Account) principal;
        } else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            account = accountService.convertToAccount(userDetails);
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        String token = jwtService.createToken(account);

        return ResultResponse.<String>builder()
                .body(token)
                .message("Login successful")
                .statusCode(200)
                .build();
    }
}
