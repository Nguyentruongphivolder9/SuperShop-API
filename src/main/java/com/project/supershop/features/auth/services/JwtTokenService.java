package com.project.supershop.features.auth.services;

import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.auth.domain.dto.response.JwtResponse;
import com.project.supershop.features.auth.domain.entities.AccessToken;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public interface JwtTokenService {
    JwtResponse createJwtResponse(Account account);
    Claims resolveClaims(String token, Key secretKey);
    String resolveToken(HttpServletRequest request);
    boolean validateClaims(Claims claims, Key secretKey) throws AuthenticationException;
    String decodePassword(String encodedPassword);
    Account parseJwtTokenToAccount(String token);
}