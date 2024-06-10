package com.project.supershop.auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.supershop.account.domain.entities.Account;
import com.project.supershop.account.services.AccountService;
import com.project.supershop.auth.JwtTokenService.JwtTokenService;
import com.project.supershop.auth.dto.request.LoginRequest;
import com.project.supershop.auth.dto.request.RegisterRequest;
import com.project.supershop.common.ResultResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/auth")
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
    )
    {
            this.authenticationManager = authenticationManager;
            this.jwtTokenService = jwtTokenService;
            this.objectMapper = objectMapper;
            this.accountService = accountService;
    }

    @PostMapping("/login")
    public ResultResponse<String> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Perform authentication
        System.out.println(loginRequest.getEmail() + " and " + loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        //Set authentication context.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Xuất ra user Details từ object principal.
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
            // Convert account object to JSON string
            String accountJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(account);
            System.out.println(accountJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String token = jwtTokenService.createToken(account);

        return ResultResponse.<String>builder()
                .body(token)
                .message("Login successful")
                .statusCode(200)
                .build();
    }

    @PostMapping("/register")
    public ResultResponse<RegisterRequest> accountRegister(@RequestBody RegisterRequest registerRequest){
        System.out.println("Role Name: " + registerRequest.getRoleName());
        System.out.println("User Name: " + registerRequest.getUserName());
        System.out.println("Password: " + registerRequest.getPassword());
        System.out.println("Avatar URL: " + registerRequest.getAvatarUrl());
        System.out.println("Full Name: " + registerRequest.getFullName());
        System.out.println("Email: " + registerRequest.getEmail());
        System.out.println("Phone Number: " + registerRequest.getPhoneNumber());
        System.out.println("Birth Day: " + registerRequest.getBirthDay());
        System.out.println("Gender: " + registerRequest.getGender());
        ResultResponse response = new ResultResponse(
                registerRequest,
                "Ok",
                201
        );

        return response;
    }

}
