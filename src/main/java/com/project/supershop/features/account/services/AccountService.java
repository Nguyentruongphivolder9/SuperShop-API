package com.project.supershop.features.account.services;

import com.project.supershop.features.account.domain.dto.request.LogoutRequest;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.auth.domain.dto.request.LoginRequest;
import com.project.supershop.features.auth.domain.dto.request.RegisterRequest;
import com.project.supershop.features.auth.domain.dto.response.EmailVerficationResponse;
import com.project.supershop.features.auth.domain.dto.response.JwtResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.List;

public interface AccountService {
    List<Account> getAllAccounts();
    Account saveAccount(RegisterRequest registerRequest);
    Account convertToAccount(UserDetails userDetails);
    //Finding interfaces
    Account findByEmail(String email);
    Account findByPhoneNumber(String phoneNumber);
    //Email verify token interfaces
    EmailVerficationResponse verifyToken(String token);
    void processNewEmailVerification(String emailTo);
    void logoutAccount(String email, String token);
    JwtResponse login(Object principal);
    boolean waitingForEmailResponse(String email);
}
