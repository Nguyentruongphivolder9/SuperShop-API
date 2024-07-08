package com.project.supershop.features.auth.controller;

import com.project.supershop.features.account.domain.dto.request.LogoutRequest;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.features.auth.domain.dto.request.EmailVerificationRequest;
import com.project.supershop.features.auth.domain.dto.response.EmailVerficationResponse;
import com.project.supershop.features.auth.services.JwtTokenService;
import com.project.supershop.features.auth.domain.dto.request.LoginRequest;
import com.project.supershop.features.auth.domain.dto.request.RegisterRequest;
import com.project.supershop.features.auth.domain.dto.response.JwtResponse;
import com.project.supershop.common.ResultResponse;
import org.joda.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.net.URI;


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

    @PostMapping("/login")
    public ResponseEntity<ResultResponse<JwtResponse>> userLogin(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Object principal = authentication.getPrincipal();

        JwtResponse jwtResponse = accountService.login(principal);
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
        JwtResponse jwtResponse = jwtTokenService.createJwtResponse(newAccount);
        return ResponseEntity.created(URI.create("")).body(
                ResultResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .body(jwtResponse)
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

    @PostMapping("/waiting-for-email-response")
    public ResponseEntity<ResultResponse> waitingForEmaiLResponse(@RequestBody LogoutRequest logoutRequest){
        boolean isValid = accountService.waitingForEmailResponse(logoutRequest.getEmail());
        return ResponseEntity.ok().body(
                ResultResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .body(isValid)
                        .message("Verify is valid")
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
