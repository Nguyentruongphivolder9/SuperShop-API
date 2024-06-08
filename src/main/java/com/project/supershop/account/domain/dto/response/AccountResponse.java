package com.project.supershop.account.domain.dto.response;

import com.project.supershop.account.domain.entities.Account;
import com.project.supershop.common.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AccountResponse implements UserDetails {
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
    private List<GrantedAuthority> authorities;

    public AccountResponse(Account account){
        this.roleName = account.getRoleName();
        this.userName = account.getUserName();
        this.password = account.getPassword();
        this.avatarUrl = account.getAvatarUrl();
        this.fullName = account.getFullName();
        this.email = account.getFullName();
        this.phoneNumber = account.getPhoneNumber();
        this.birthDay = account.getBirthDay();
        this.gender = account.getGender();
        this.isActive = account.getIsActive();

        this.authorities = Arrays.stream()
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }
}
