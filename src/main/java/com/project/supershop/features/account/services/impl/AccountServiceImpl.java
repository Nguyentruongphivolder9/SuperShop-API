package com.project.supershop.features.account.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.repositories.AccountRepositories;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.features.auth.dto.request.RegisterRequest;
import com.project.supershop.features.auth.dto.response.EmailVerficationResponse;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final EmailService emailService;
    private final AccountRepositories accountRepositories;
    private final ConfirmationRepository confirmationRepository;
    private final EmailRepository emailRepository;
    private EmailVerficationResponse emailVerficationResponse;
    private final ObjectMapper objectMapper;

    public AccountServiceImpl(AccountRepositories accountRepositories, ConfirmationRepository confirmationRepository, EmailService emailService, EmailRepository emailRepository,  ObjectMapper objectMapper) {
        this.accountRepositories = accountRepositories;
        this.confirmationRepository = confirmationRepository;
        this.emailService = emailService;
        this.emailRepository = emailRepository;
        this.objectMapper = objectMapper;
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
    public EmailVerficationResponse verifyToken(String token) {
        //Response message cho HTML Mail sender.
        EmailVerficationResponse emailResponse = new EmailVerficationResponse();
        Confirmation confirmation = confirmationRepository.findConfirmationByToken(token);
        Email email = emailRepository.findEmailByConfirmations(confirmation);

        LocalDateTime date = null;
        if (confirmation == null) {
            emailResponse.setType("Not Found");
            emailResponse.setMessage("Confirmation token not found.");
            return emailResponse;
        }
        LocalDateTime expiredDay = confirmation.getExpiredDay();
        LocalDateTime now = LocalDateTime.now();
        emailResponse.setEmail(email.getEmailAddress());
        if (expiredDay.isBefore(now)) {
            emailResponse.setType("Expired");
            emailResponse.setMessage("Verification link expired.");
        } else {
            email.setVerified(true);
            emailResponse.setType("Fine");
            emailResponse.setMessage("Verification successful.");
            confirmation.setVerify(true);
            confirmation.setUpdatedAt(date.now());
            confirmationRepository.save(confirmation);
            emailRepository.save(email);

        }
        return emailResponse;
    }

    @Override
    public void processNewEmailVerification(String emailTo) {
        Optional<Account> accountExists = accountRepositories.findAccountByEmail(emailTo);
        if (!accountExists.isPresent()) {
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

            // Send email
            emailService.sendHtmlEmail("New User", emailTo, emailConfirm.getToken());
        } else {
            throw new RuntimeException("Email already verified for another account");
        }
    }

    @Override
    public void logoutAccount(String token) {
        try {
            String[] parts = token.split("\\.");
            String encodedPayload = parts[1];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedPayload);
            String decodedPayload = new String(decodedBytes, "UTF-8");

            // Deserialize JSON payload to a map
            Map<String, Object> payloadMap = objectMapper.readValue(decodedPayload, new TypeReference<Map<String, Object>>() {});

            // Extract necessary fields from the payload map
            Integer accountId = (Integer) payloadMap.get("id");

            // Fetch account from database using accountId
            Account account = accountRepositories.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found for id: " + accountId));

            // Perform logout action (e.g., set isActive to false)
            account.setIsActive(false);
            accountRepositories.save(account);
        } catch (Exception e) {
            throw new RuntimeException("Could not decode or access JWT token payload", e);
        }
    }

    /**
     * saveAccount
     * <p>
     * Mô tả:
     * Đây là bước gân cuối, chỉ sau bước xác thực thông qua email để enable tài khoảng. Hàm saveAccount
     * sẽ tạo tài khoản trong khi 1 hàm gửi email là sendSimpleMailMessage sẽ được chạy 1 cách không đồng bộ với
     * hàm saveAccount, để cả thiện thời gian đợi. Thay vì đợi cả 2 hàm tạo tài khoản và gửi mail xác nhận được thành công thì mới
     * trả response cho client, thì sendSimpleMailMessage sẽ được chạy trên 1 sync khác.
     *
     * @param registerRequest 1 DTO request cho việc register
     * @return 1 đối tượng kiểu Account
     * @throws RuntimeException Nếu như email đã được sử dụng
     *                          <p>
     *                          Tác giả: Trần Anh Tiến
     *                          Ngày tạo: 16-06-2024
     */
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
