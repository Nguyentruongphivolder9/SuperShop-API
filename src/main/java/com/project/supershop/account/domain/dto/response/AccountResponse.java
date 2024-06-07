package com.project.supershop.account.domain.dto.response;

import com.project.supershop.account.domain.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountResponse {
    private Account.Roles roleName;
    private String userName;
    private String password;
    private String avatarUrl;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDateTime birthDay;
    private String gender;
    private Boolean isActive;
}
