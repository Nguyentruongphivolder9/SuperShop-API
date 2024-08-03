package com.project.supershop.features.account.services.impl;

import com.project.supershop.features.account.domain.dto.request.WaitingForEmailVerifyRequest;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.repositories.AccountRepositories;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.features.account.utils.enums.Provider;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
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
    private static final String FEMALE_DEFAULT_URL_AVATAR = "http://localhost:8080/api/v1/avatar/static/defaultAvatar/femaleAvatar/female.png";
    private static final String MALE_DEFAULT_URL_AVATAR = "http://localhost:8080/api/v1/avatar/static/defaultAvatar/maleAvatar/male.png";

    private JwtTokenService jwtTokenService;
    private AccessTokenService accessTokenService;

    @Autowired
    public AccountServiceImpl(AccountRepositories accountRepositories, ConfirmationRepository confirmationRepository, EmailService emailService, EmailRepository emailRepository) {
        this.accountRepositories = accountRepositories;
        this.confirmationRepository = confirmationRepository;
        this.emailService = emailService;
        this.emailRepository = emailRepository;
    }

    @Autowired
    public void setJwtTokenService(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Autowired
    public void setAccessTokenService(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepositories.findAll();
    }

    @Override
    public Account findByEmail(String email) {
        return accountRepositories.findAccountByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found with email: " + email));
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
        Account account = accountRepositories.findAccountByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found for email: " + email));

        account.setIsActive(false);
        account.setIsLoggedOut(true);
        accountRepositories.save(account);

        Optional<AccessToken> accessToken = accessTokenService.findByToken(token);
        if (accessToken.isEmpty()) {
            throw new RuntimeException("Invalid Bearer Token");
        }

        accessTokenService.deleteByToken(accessToken.get().getToken());
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

        account.setIsActive(true);
        account.setIsLoggedOut(false);
        accountRepositories.save(account);

        JwtResponse jwtResponse = jwtTokenService.createJwtResponse(account);
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
    public boolean waitingForEmailResponse(WaitingForEmailVerifyRequest request) {
        Email email = emailRepository.findEmailByEmailAddress(request.getEmail());
        if (email == null) {
            return false;
        }

        Confirmation confirmation = confirmationRepository.findConfirmationByEmailAndToken(email, request.getToken());
        return confirmation != null && confirmation.isVerify();
    }

    @Override
    public Account createOrMergeGoogleAccountToLocalAccount(Account accountFromGoogle) {
        Optional<Account> optionalAccount = accountRepositories.findAccountByEmail(accountFromGoogle.getEmail());
        if (optionalAccount.isEmpty()) {
            accountFromGoogle.setProvider(Provider.GOOGLE.getValue());
            accountFromGoogle.setIsLoggedOut(true);
            return accountRepositories.save(accountFromGoogle);
        } else {
            Account localAccount = optionalAccount.get();
            localAccount.setUserName(accountFromGoogle.getUserName());
            localAccount.setFullName(accountFromGoogle.getFullName());
            localAccount.setAvatarUrl(accountFromGoogle.getAvatarUrl());
            localAccount.setProvider(Provider.GOOGLE.getValue());
            return accountRepositories.save(localAccount);
        }
    }


    @Override
    public Account saveAccount(RegisterRequest request) {
        if (accountRepositories.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        request.setEnable(false);

        Account account = new Account();
        account.setUserName(request.getUser_name());
        account.setFullName(request.getFull_name());
        account.setPassword(request.getPassword());
        account.setPhoneNumber(request.getPhone_number());
        account.setEmail(request.getEmail());
        account.setProvider(Provider.LOCAL.getValue());
        account.setIsEnable(false);
        account.setGender(request.getGender());
        account.setAvatarUrl(request.getGender().equals("male") ? MALE_DEFAULT_URL_AVATAR : FEMALE_DEFAULT_URL_AVATAR);
        account.setBirthDay(parseStringToLocalDateTime(request.getBirth_day()));
        account.setProvider(Provider.LOCAL.getValue());
        account.setRoleName("USER");
        account.setIsActive(request.isActive());
        account.setIsLoggedOut(true);

        return accountRepositories.save(account);
    }

    @Override
    public Account convertToAccount(UserDetails userDetails) {
        return accountRepositories.findAccountByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found with email: " + userDetails.getUsername()));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = findByEmail(email);

        List<SimpleGrantedAuthority> authorities = Stream.of(account.getRoleName().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPassword(),
                authorities
        );
    }

    private LocalDateTime parseStringToLocalDateTime(String dateString) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString);
            return zonedDateTime.toLocalDateTime();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid birth_day format", e);
        }
    }

    @Override
    public EmailVerficationResponse verifyToken(String token) {
        EmailVerficationResponse response = new EmailVerficationResponse();
        Confirmation confirmation = confirmationRepository.findConfirmationByToken(token);

        if (confirmation == null) {
            response.setType("Not Found");
            response.setMessage("Confirmation token not found.");
            return response;
        }

        Email email = emailRepository.findEmailByConfirmations(confirmation);
        if (email == null) {
            response.setType("Not Found");
            response.setMessage("Email not found for confirmation token.");
            return response;
        }

        LocalDateTime expiredDay = confirmation.getCreatedAt().plusMinutes(15);
        if (expiredDay.isBefore(LocalDateTime.now())) {
            response.setType("Expired");
            response.setMessage("Confirmation token has expired.");
            return response;
        }

        if (!confirmation.isVerify()) {
            Account account = new Account();
            account.setEmail(email.getEmailAddress());
            account.setIsEnable(true);
            account.setIsActive(false);
            account.setIsLoggedOut(true);
            accountRepositories.save(account);

            confirmation.setVerify(true);
            confirmationRepository.save(confirmation);

            response.setType("Valid");
            response.setMessage("Email verification successful.");
        } else {
            response.setType("Valid");
            response.setMessage("Email is already verified.");
        }

        return response;
    }
}
