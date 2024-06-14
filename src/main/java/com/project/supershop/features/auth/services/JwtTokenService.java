package com.project.supershop.features.auth.services;

import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.auth.dto.response.JwtResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public interface JwtTokenService {
    JwtResponse createJwtResponse(Account account);
    Claims resolveClaims(HttpServletRequest req);
    String resolveToken(HttpServletRequest request);
    boolean validateClaims(Claims claims) throws AuthenticationException;
    String decodePassword(String encodedPassword);
}
