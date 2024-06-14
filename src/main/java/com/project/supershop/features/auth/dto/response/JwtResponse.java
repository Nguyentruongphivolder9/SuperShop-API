package com.project.supershop.features.auth.dto.response;

import com.project.supershop.features.account.domain.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private long expireRefreshToken;
    private long expires;
    private Account account;
}
