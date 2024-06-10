package com.project.supershop.account.controller;

import com.project.supershop.account.domain.entities.Account;
import com.project.supershop.account.services.AccountService;
import com.project.supershop.auth.JWT.JwtTokenService;
import com.project.supershop.account.domain.dto.request.AuthRequest;
import com.project.supershop.common.ResultResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final JwtTokenService jwtService;

    private final AuthenticationManager authenticationManager;

    private final AccountService accountService;

    //Initilization...
    public AccountController(AccountService accountService, JwtTokenService jwtService, AuthenticationManager authenticationManager1) {
        this.jwtService = jwtService;
        this.accountService = accountService;
        this.authenticationManager = authenticationManager1;
    }

    @GetMapping
    public ResultResponse<Iterable<Account>> getAllAccounts() {
        return new ResultResponse<Iterable<Account>>(accountService.GetAllAccounts(),"Get all accounts sucessfully", 200 );
    }

    @PostMapping
    public String authenticationAndGetToken(@RequestBody AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUserName(),
                        authRequest.getPassword()
                ));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUserName());
        } else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

}
