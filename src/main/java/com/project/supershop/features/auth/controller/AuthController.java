package com.project.supershop.features.auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.supershop.features.account.domain.dto.response.AccountJwtParsingResponse;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.repositories.AccountRepositories;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.features.auth.dto.request.EmailVerificationRequest;
import com.project.supershop.features.auth.dto.response.EmailVerficationResponse;
import com.project.supershop.features.auth.services.JwtTokenService;
import com.project.supershop.features.auth.dto.request.LoginRequest;
import com.project.supershop.features.auth.dto.request.RegisterRequest;
import com.project.supershop.features.auth.dto.response.JwtResponse;
import com.project.supershop.common.ResultResponse;
import com.project.supershop.features.email.domain.entities.Confirmation;
import com.project.supershop.features.email.repositories.ConfirmationRepository;
import com.project.supershop.features.email.sevices.EmailService;
import com.project.supershop.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    private final EmailService emailService;
    private final ConfirmationRepository confirmationRepository;
    private final AccountRepositories accountRepositories;
    public AuthController(
            //============Connectors==================.
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            ObjectMapper objectMapper,
            AccountService accountService,
            EmailService emailService,
            ConfirmationRepository confirmationRepository,
            AccountRepositories accountRepositories
            //========================================
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.objectMapper = objectMapper;
        this.accountService = accountService;
        this.emailService = emailService;
        this.confirmationRepository = confirmationRepository;
        this.accountRepositories = accountRepositories;
    }

    @PostMapping("/login")
    public ResponseEntity<ResultResponse<JwtResponse>> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Object principal = authentication.getPrincipal();
        Account account;

        if (principal instanceof Account) {
            account = (Account) principal;
        } else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            account = accountService.convertToAccount(userDetails);
            account.setIsActive(true);
            accountRepositories.save(account);
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        try {
            String accountJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(account);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create JWT response
        JwtResponse jwtResponse = jwtTokenService.createJwtResponse(account);

        return ResponseEntity.ok(
                ResultResponse.<JwtResponse>builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .body(jwtResponse)
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
                        .body(Map.of("data", jwtResponse))
                        .message("Register successful")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );

    }

    @PostMapping("/send-email")
    public ResponseEntity<ResultResponse> sendEmailVerifycation(@RequestBody EmailVerificationRequest emailVerificationRequest) {
        accountService.processNewEmailVerification(emailVerificationRequest.getEmail());
        return ResponseEntity.ok().body(
                ResultResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .body(null)
                        .message("Verification has been send to your email address.")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );

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
                modelAndView.addObject("error", response.getMessage());
                modelAndView.addObject("message", "Email không tìm thấy");
                modelAndView.setViewName("VerifyError");
                break;
            case "Expired":
                modelAndView.addObject("error", response.getMessage());
                modelAndView.addObject("message", "Xác thực cho email " + response.getEmail() + " đã hết hạn");
                modelAndView.setViewName("VerifyError");
                break;
            default:
                modelAndView.addObject("error", "Unknown error");
                modelAndView.addObject("message", "Đã xảy ra lỗi không xác định");
                modelAndView.setViewName("VerifyError");
                break;
        }

        return modelAndView;
    }



}
