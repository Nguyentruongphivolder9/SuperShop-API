package com.project.supershop.features.account.services;

import com.project.supershop.features.account.domain.dto.request.WaitingForEmailVerifyRequest;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.repositories.AccountRepositories;
import com.project.supershop.features.auth.domain.dto.request.RegisterRequest;
import com.project.supershop.features.auth.domain.dto.response.EmailVerficationResponse;
import com.project.supershop.features.auth.domain.dto.response.JwtResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface AccountService{
    List<Account> getAllAccounts();
    Account saveAccount(Account account);
    Account convertToAccount(UserDetails userDetails);
    //Finding interfaces
    Account findByEmail(String email);

    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    //Email verify token interfaces
    EmailVerficationResponse verifyToken(String token);
    String processNewEmailVerification(String emailTo);
    void logoutAccount(String email, String token);
    JwtResponse login(Object principal);
    boolean waitingForEmailResponse(WaitingForEmailVerifyRequest waitingForEmailVerifyRequest);
    Account createOrMergeGoogleAccountToLocalAccount(Account accountFromGoogle);
    Account registerAccount(RegisterRequest registerRequest);
    String encodeAccountPassword(String password);
    String decodeAccountPassword(String password);
    JwtResponse refreshToken (String refreshToken);
}
