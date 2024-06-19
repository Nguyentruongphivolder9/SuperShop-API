package com.project.supershop.features.auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.features.auth.services.JwtTokenService;
import com.project.supershop.features.auth.dto.request.LoginRequest;
import com.project.supershop.features.auth.dto.request.RegisterRequest;
import com.project.supershop.features.auth.dto.response.JwtResponse;
import com.project.supershop.common.ResultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final JwtTokenService jwtTokenService;
    private final AccountService accountService;

    public AuthController(
            //============Connectors==================.
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            ObjectMapper objectMapper,
            AccountService accountService
            //========================================
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.objectMapper = objectMapper;
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResultResponse<JwtResponse>> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Extract user details from the principal object
        Object principal = authentication.getPrincipal();
        Account account;

        if (principal instanceof Account) {
            account = (Account) principal;
        } else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            account = accountService.convertToAccount(userDetails);
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        try {
            String accountJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(account);
            System.out.println(accountJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create JWT response
        JwtResponse jwtResponse = jwtTokenService.createJwtResponse(account);

        return ResponseEntity.ok(
                ResultResponse.<JwtResponse>builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(jwtResponse)
                        .message("Authentication successfully")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }


    @PostMapping("/register")
    public ResponseEntity<ResultResponse> accountRegister(@RequestBody RegisterRequest registerRequest) {
        Account newAccount = accountService.saveAccount(registerRequest);
        //Return JwtToken when register.
        JwtResponse jwtResponse = jwtTokenService.createJwtResponse(newAccount);
        return ResponseEntity.created(URI.create("")).body(
                ResultResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("data", jwtResponse))
                        .message("Register successful")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );

    }

    @GetMapping
    public ResponseEntity<ResultResponse> confirmUserAccount(@RequestParam("token") String token){
        Boolean isSuccess = accountService.verifyToken(token);
        return ResponseEntity.ok().body(
                ResultResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("data", isSuccess))
                        .message("Email confirmation successfully.")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }


}
