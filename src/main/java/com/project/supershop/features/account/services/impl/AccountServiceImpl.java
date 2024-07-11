package com.project.supershop.features.account.services.impl;

import com.project.supershop.features.account.domain.dto.request.WaitingForEmailVerifyRequest;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.repositories.AccountRepositories;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.features.auth.domain.dto.request.RegisterRequest;
import com.project.supershop.features.auth.domain.dto.response.EmailVerficationResponse;
import com.project.supershop.features.auth.domain.dto.response.JwtResponse;
import com.project.supershop.features.auth.domain.entities.AccessToken;
import com.project.supershop.features.auth.services.AccessTokenService;
import com.project.supershop.features.auth.services.JwtTokenService;
import com.project.supershop.features.email.domain.entities.Confirmation;
import com.project.supershop.features.email.domain.entities.Email;
import com.project.supershop.features.email.repositories.ConfirmationRepository;
import com.project.supershop.features.email.repositories.EmailRepository;
import com.project.supershop.features.email.sevices.EmailService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final EmailService emailService;
    private final AccountRepositories accountRepositories;
    private final ConfirmationRepository confirmationRepository;
    private final EmailRepository emailRepository;

    private final JwtTokenService jwtTokenService;
    private final AccessTokenService accessTokenService;

    public AccountServiceImpl(AccountRepositories accountRepositories, ConfirmationRepository confirmationRepository, EmailService emailService, EmailRepository emailRepository, JwtTokenService jwtTokenService, AccessTokenService accessTokenService) {
        this.accountRepositories = accountRepositories;
        this.confirmationRepository = confirmationRepository;
        this.emailService = emailService;
        this.emailRepository = emailRepository;
        this.jwtTokenService = jwtTokenService;
        this.accessTokenService = accessTokenService;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepositories.findAll();
    }

    @Override
    public Account findByEmail(String email) {
        return accountRepositories.findAccountByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("Account not found with email: " + email));
    }

    @Override
    public Account findByPhoneNumber(String phoneNumber) {
        return accountRepositories.findAccountByPhoneNumber(phoneNumber).orElseThrow(() ->
                new UsernameNotFoundException("Account not found with user phone number: " + phoneNumber));
    }

    public EmailVerficationResponse verifyToken(String token) {
        EmailVerficationResponse emailResponse = new EmailVerficationResponse();
        Confirmation confirmation = confirmationRepository.findConfirmationByToken(token);

        if (confirmation == null) {
            emailResponse.setType("Not Found");
            emailResponse.setMessage("Confirmation token not found.");
            return emailResponse;
        }

        Email email = emailRepository.findEmailByConfirmations(confirmation);
        if (email == null) {
            emailResponse.setType("Not Found");
            emailResponse.setMessage("Email not found for confirmation token.");
            return emailResponse;
        }

        LocalDateTime expiredDay = confirmation.getExpiredDay();
        LocalDateTime now = LocalDateTime.now();
        emailResponse.setEmail(email.getEmailAddress());

        if (expiredDay.isBefore(now)) {
            emailResponse.setType("Expired");
            emailResponse.setMessage("Verification link expired.");
        } else if (confirmation.isVerify()) {
            emailResponse.setType("Verified");
            emailResponse.setMessage("Verification link was already verified.");
        } else {
            emailRepository.save(email);
            confirmation.setVerify(true);
            confirmationRepository.save(confirmation);
            emailResponse.setType("Fine");
            emailResponse.setMessage("Verification successful.");
        }

        return emailResponse;
    }

    @Override
    public String processNewEmailVerification(String emailTo) {
        Optional<Account> accountExists = accountRepositories.findAccountByEmail(emailTo);
        if (accountExists.isEmpty()) {
            Email email = emailRepository.findEmailByEmailAddress(emailTo);
            if (email == null) {
                email = new Email();
                email.setEmailAddress(emailTo);
            }

            Confirmation emailConfirm = new Confirmation();
            emailConfirm.setEmail(email);

            confirmationRepository.save(emailConfirm);
            List<Confirmation> confirmationList = email.getConfirmations();
            if (confirmationList == null) {
                confirmationList = new ArrayList<>();
                email.setConfirmations(confirmationList);
            }
            confirmationList.add(emailConfirm);
            emailRepository.save(email);
            emailService.sendHtmlEmail("New User", emailTo, emailConfirm.getToken());
            return emailConfirm.getToken();

        } else {
            throw new RuntimeException("Email already verified for another account");
        }
    }
    @Override
    public void logoutAccount(String email, String token) {
        Optional<Account> optionalAccount = accountRepositories.findAccountByEmail(email);
        if (!optionalAccount.isPresent()) {
            throw new RuntimeException("Account not found for email: " + email);
        }

        Account account = optionalAccount.get();
        account.setIsActive(false);
        account.setIsLoggedOut(true);
        accountRepositories.save(account);

        AccessToken accessToken = accessTokenService.findByToken(token);
        if (accessToken == null) {
            throw new RuntimeException("Invalid Bearer Token");
        }

        accessTokenService.deleteByToken(token);
    }

    @Override
    public JwtResponse login(Object principal) {
        Account account;
        if (principal instanceof Account) {
            account = (Account) principal;
        } else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            account = convertToAccount(userDetails);
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }
//        if (!account.getIsLoggedOut()) {
//            throw new RuntimeException("Account is already logged in.");
//        }
        account.setIsActive(true);
        account.setIsLoggedOut(false);
        accountRepositories.save(account);
        JwtResponse jwtResponse = jwtTokenService.createJwtResponse(account);
        System.out.println("==================> Checking " + jwtResponse.getAccessToken());
        AccessToken accessToken = AccessToken.builder()
                .token(jwtResponse.getAccessToken())
                .refreshToken(jwtResponse.getRefreshToken())
                .issuedAt(System.currentTimeMillis())
                .expiresAt(jwtResponse.getExpires())
                .build();

        accessTokenService.saveToken(accessToken);

        return jwtResponse;
    }

    @Override
    public boolean waitingForEmailResponse(WaitingForEmailVerifyRequest waitingForEmailVerifyRequest) {
        Email emailFinding = emailRepository.findEmailByEmailAddress(waitingForEmailVerifyRequest.getEmail());
        if (emailFinding == null) {
            return false;
        }

        Confirmation emailConfirmation = confirmationRepository.findConfirmationByEmailAndToken(emailFinding, waitingForEmailVerifyRequest.getToken());
        if (emailConfirmation == null) {
            return false;
        }

        return emailConfirmation.isVerify();
    }


    @Override
    public Account saveAccount(RegisterRequest registerRequest) {
        if (accountRepositories.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        registerRequest.setEnable(false);
        Account accountSaving = new Account();
        accountSaving.setUserName(registerRequest.getUserName());
        accountSaving.setFullName(registerRequest.getFullName());
        accountSaving.setPassword(registerRequest.getPassword());
        accountSaving.setAvatarUrl(registerRequest.getAvatarUrl());
        accountSaving.setPhoneNumber(registerRequest.getPhoneNumber());
        accountSaving.setEmail(registerRequest.getEmail());
        accountSaving.setIsEnable(false);
        accountSaving.setGender(registerRequest.getGender());
        accountSaving.setBirthDay(registerRequest.getBirthDay());
        accountSaving.setRoleName("USER");
        accountSaving.setIsActive(registerRequest.isActive());
        accountSaving.setIsLoggedOut(true);
        accountRepositories.save(accountSaving);

        return accountSaving;
    }

    @Override
    public Account convertToAccount(UserDetails userDetails) {
        return accountRepositories.findAccountByEmail(userDetails.getUsername()).orElseThrow(() ->
                new UsernameNotFoundException("Account not found with email: " + userDetails.getUsername()));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        List<SimpleGrantedAuthority> authorities = Stream.of(account.getRoleName().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPassword(),
                authorities
        );
    }
}
