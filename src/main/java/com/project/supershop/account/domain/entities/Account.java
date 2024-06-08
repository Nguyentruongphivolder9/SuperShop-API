package com.project.supershop.account.domain.entities;

import com.project.supershop.account.domain.dto.request.AccountRequest;
import com.project.supershop.common.BaseEntity;
import com.project.supershop.common.enums.Roles;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class Account extends BaseEntity {
    private Roles roleName;
    private String userName;
    private String password;
    private String avatarUrl;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDateTime birthDay;
    private String gender;
    private Boolean isActive;

    public static Account createAccount(AccountRequest accountRequest){
        Roles initializeRole = Roles.USER;
        return Account.builder()
                .roleName(initializeRole)
                .userName(accountRequest.getUserName())
                .password(accountRequest.getPassword())
                .avatarUrl(accountRequest.getAvatarUrl())
                .fullName(accountRequest.getFullName())
                .email(accountRequest.getEmail())
                .phoneNumber(accountRequest.getPhoneNumber())
                .birthDay(accountRequest.getBirthDay())
                .gender(accountRequest.getGender())
                .isActive(accountRequest.getIsActive())
                .build();
    }
}
