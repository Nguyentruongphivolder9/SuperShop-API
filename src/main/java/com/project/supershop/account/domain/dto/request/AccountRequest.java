package com.project.supershop.account.domain.dto.request;

import com.project.supershop.account.domain.entities.Account;
import com.project.supershop.common.enums.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountRequest {

    @NotNull(message = "User role name can't be left empty")
    private Roles roleName;

    @NotBlank(message = "User name can't be blank")
    @NotNull(message = "User name can't be left empty")
    private String userName;

    @NotBlank(message = "User password can't be blank")
    @NotNull(message = "User password can't be left empty")
    private String password;

    @NotNull(message = "User avatarURL can't be left empty")
    private String avatarUrl;

    @NotBlank(message = "User full name can't be blank")
    @NotNull(message = "User full name can't be left empty")
    private String fullName;

    @NotBlank(message = "User email can't be blank")
    @NotNull(message = "User email can't be left empty")
    private String email;

    @NotBlank(message = "User phone number can't be blank")
    @NotNull(message = "User phone number can't be left empty")
    private String phoneNumber;

    private LocalDateTime birthDay;

    @NotNull(message = "User gender can't be left empty")
    private String gender;

    private Boolean isActive;



}
