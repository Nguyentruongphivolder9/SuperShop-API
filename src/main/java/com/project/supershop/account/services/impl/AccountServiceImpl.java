package com.project.supershop.account.services.impl;

import com.project.supershop.account.domain.entities.Account;
import com.project.supershop.account.repositories.AccountRepositories;
import com.project.supershop.account.services.AccountService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final AccountRepositories accountRepositories;

    public AccountServiceImpl(AccountRepositories accountRepositories) {
        this.accountRepositories = accountRepositories;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepositories.findAll();
    }

//    @Override
//    public Account findByName(String name) {
//        return accountRepositories.findByName(name).orElseThrow(() ->
//                new UsernameNotFoundException("Account not found with name: " + name));
//    }

    @Override
    public Account findByEmail(String email) {
        return accountRepositories.findAccountByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("Account not found with email: " + email));
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
