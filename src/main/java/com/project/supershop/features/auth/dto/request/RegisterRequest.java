package com.project.supershop.features.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.supershop.common.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest extends BaseEntity {
    @Pattern(regexp = "ADMIN|USER|SELLER")
    private String roleName;

    @NotBlank(message = "Name can't be blank")
    @NotNull(message = "Name can't be left empty")
    private String userName;

    @NotBlank(message = "Password can't be blank")
    @NotNull(message = "Password can't be left empty")
    private String password;

    @NotNull(message = "User avatarURL can't be left empty")
    private String avatarUrl;

    @NotBlank(message = "User full name can't be blank")
    @NotNull(message = "User full name can't be left empty")
    private String fullName;

    @NotBlank(message = "Email can't be blank")
    @NotNull(message = "Email can't be left empty")
    private String email;

    @NotBlank(message = "Phone number can't be blank")
    @NotNull(message = "Phone number can't be left empty")
    private String phoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime birthDay;

    @NotNull(message = "User gender can't be left empty")
    private String gender;

    private boolean isActive = true;
    private boolean isEnable;
}
