package com.project.supershop.features.auth.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.project.supershop.features.account.domain.dto.request.WaitingForEmailVerifyRequest;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.features.auth.domain.dto.request.EmailVerificationRequest;
import com.project.supershop.features.auth.domain.dto.response.EmailVerficationResponse;
import com.project.supershop.features.auth.domain.dto.response.TokenDto;
import com.project.supershop.features.auth.domain.dto.response.UrlDto;
import com.project.supershop.features.auth.services.JwtTokenService;
import com.project.supershop.features.auth.domain.dto.request.LoginRequest;
import com.project.supershop.features.auth.domain.dto.request.RegisterRequest;
import com.project.supershop.features.auth.domain.dto.response.JwtResponse;
import com.project.supershop.common.ResultResponse;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final JwtTokenService jwtTokenService;
    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            AccountService accountService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.accountService = accountService;

    }

    @PostMapping("find-account-by-token")
    public ResponseEntity<ResultResponse<Account>> retrunAccountByToken(@RequestParam("token") String token) {
        Account accountFinding = null;
        try {
            accountFinding = jwtTokenService.parseJwtTokenToAccount(token);
            return ResponseEntity.ok(
                    ResultResponse.<Account>builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .body(accountFinding)
                            .message("Found a account")
                            .status(HttpStatus.UNAUTHORIZED)
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResultResponse.<Account>builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .body(null)
                            .message("Error finding account: " + e.getMessage())
                            .status(HttpStatus.UNAUTHORIZED)
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }


    @PostMapping("/login")
    public ResponseEntity<ResultResponse<JwtResponse>> userLogin(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            JwtResponse jwtResponse = accountService.login(authentication.getPrincipal());
            return ResponseEntity.ok(
                    ResultResponse.<JwtResponse>builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .body(jwtResponse)
                            .message("Authentication successfully")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResultResponse.<JwtResponse>builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .body(null)
                            .message("Error logging in: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build()
            );
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResultResponse<JwtResponse>> accountRegister(@RequestBody RegisterRequest registerRequest) {
        try {
            Account newAccount = accountService.saveAccount(registerRequest);
            JwtResponse jwtResponse = jwtTokenService.createJwtResponse(newAccount);
            return ResponseEntity.created(URI.create("")).body(
                    ResultResponse.<JwtResponse>builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .body(jwtResponse)
                            .message("Register successful")
                            .status(HttpStatus.CREATED)
                            .statusCode(HttpStatus.CREATED.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResultResponse.<JwtResponse>builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .body(null)
                            .message("Error registering: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build()
            );
        }
    }

    @PostMapping("/send-email")
    public ResponseEntity<ResultResponse<?>> sendEmailVerification(@RequestBody EmailVerificationRequest emailVerificationRequest) {
        try {
            String token = accountService.processNewEmailVerification(emailVerificationRequest.getEmail());
            return ResponseEntity.ok(
                    ResultResponse.<String>builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .body(token)
                            .message("Verification email has been sent.")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResultResponse.<Void>builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .body(null)
                            .message("Error sending verification email: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build()
            );
        }
    }

    @PostMapping("/waiting-for-email-response")
    public ResponseEntity<ResultResponse<Boolean>> waitingForEmailResponse(@RequestBody WaitingForEmailVerifyRequest emailVerifyRequest) {
        System.out.print(emailVerifyRequest.getEmail());
        System.out.print(emailVerifyRequest.getToken());
        try {
            boolean isValid = accountService.waitingForEmailResponse(emailVerifyRequest);
            return ResponseEntity.ok(
                    ResultResponse.<Boolean>builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .body(isValid)
                            .message(isValid ? "Verification is valid" : "Verification is not valid")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResultResponse.<Boolean>builder()
                            .timeStamp(LocalDateTime.now().toString())
                            .body(false)
                            .message("Error checking email verification: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build()
            );
        }
    }

    @GetMapping("/verify-email")
    public ModelAndView verifyEmail(@RequestParam("token") String token) {
        ModelAndView modelAndView = new ModelAndView();
        EmailVerficationResponse response = accountService.verifyToken(token);

        modelAndView.addObject("email", response.getEmail());

        switch (response.getType()) {
            case "Fine":
                modelAndView.addObject("message", "Xác thực cho email " + response.getEmail() + " thành công");
                modelAndView.setViewName("VerifySuccess");
                break;
            case "Not Found":
            case "Expired":
            default:
                modelAndView.addObject("error", response.getMessage());
                modelAndView.addObject("message", "Xác thực email không thành công");
                modelAndView.setViewName("VerifyError");
                break;
        }

        return modelAndView;
    }


}
