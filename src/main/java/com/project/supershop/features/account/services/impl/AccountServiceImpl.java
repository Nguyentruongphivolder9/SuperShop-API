package com.project.supershop.features.account.services.impl;

import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.repositories.AccountRepositories;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.features.auth.dto.request.RegisterRequest;
import com.project.supershop.features.email.domain.entities.Confirmation;
import com.project.supershop.features.email.repositories.ConfirmationRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final AccountRepositories accountRepositories;
    private final ConfirmationRepository confirmationRepository;

    public AccountServiceImpl(AccountRepositories accountRepositories, ConfirmationRepository confirmationRepository) {
        this.accountRepositories = accountRepositories;
        this.confirmationRepository = confirmationRepository;
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

    @Override
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        if (confirmation == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        Optional<Account> accountOpt = accountRepositories.findAccountByEmail(confirmation.getAccount().getEmail());
        Account account = accountOpt.orElseThrow(() ->
                new UsernameNotFoundException("Account not found with user email: " + confirmation.getAccount().getEmail()));

        account.setIsEnable(true);
        accountRepositories.save(account);
        return Boolean.TRUE;
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
        accountRepositories.save(accountSaving);
        Confirmation confirmation = new Confirmation(accountSaving);
        confirmationRepository.save(confirmation);
        /*
         * Send email confirmation to user email
         * Sending
         * */
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
