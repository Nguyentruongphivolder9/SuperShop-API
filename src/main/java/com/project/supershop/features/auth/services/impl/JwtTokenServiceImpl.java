package com.project.supershop.features.auth.services.impl;

import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.account.services.AccountService;
import com.project.supershop.features.auth.domain.dto.response.JwtResponse;
import com.project.supershop.features.auth.domain.entities.AccessToken;
import com.project.supershop.features.auth.providers.SecretKeyProvider;
import com.project.supershop.features.auth.repositories.AccessTokenRepository;
import com.project.supershop.features.auth.services.AccessTokenService;
import com.project.supershop.features.auth.services.JwtTokenService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class JwtTokenServiceImpl implements JwtTokenService, AccessTokenService {
    private final long accessTokenValidity = 24 * 60 * 60 * 1000; // 1 day
    private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000; // 7 days

    private final SecretKeyProvider secretKeyProvider;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";
    private final AccessTokenRepository accessTokenRepository;

    private AccountService accountService;

    public JwtTokenServiceImpl(AccessTokenRepository accessTokenRepository, SecretKeyProvider secretKeyProvider) {
        this.accessTokenRepository = accessTokenRepository;
        this.secretKeyProvider = secretKeyProvider;
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }


    @Override
    public JwtResponse createJwtResponse(Account account) {
        Key secretKey = secretKeyProvider.getSecretKey();
        Claims claims = Jwts.claims().setSubject(account.getEmail());
        claims.put("userName", account.getUserName());
        claims.put("fullName", account.getFullName());
        claims.put("email", account.getEmail());
        claims.put("role", account.getRoleName());
        claims.put("phoneNumber", account.getPhoneNumber());
        System.out.print("Phone number from Jwt Create BE : " + account.getPhoneNumber());

        claims.put("gender", account.getGender());
        claims.put("avatarUrl", account.getAvatarUrl());
        claims.put("isActive", account.getIsActive() ? "Online" : "Offline");

        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + accessTokenValidity);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(secretKey)
                .compact();

        Date refreshTokenExpiry = new Date(tokenCreateTime.getTime() + refreshTokenValidity);
        String refreshToken = Jwts.builder()
                .setSubject(account.getEmail())
                .setExpiration(refreshTokenExpiry)
                .signWith(secretKey)
                .compact();

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setAccessToken(accessToken);
        jwtResponse.setRefreshToken(refreshToken);
        jwtResponse.setExpireRefreshToken(refreshTokenExpiry.getTime());
        jwtResponse.setExpires(tokenValidity.getTime());
        jwtResponse.setAccount(account);
        return jwtResponse;
    }

    @Override
    public Claims resolveClaims(String token, Key secretKey) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Invalid JWT token or claims", e);
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    @Override
    public boolean validateClaims(Claims claims, Key secretKey) throws AuthenticationException {
        try {
            boolean isValid = claims.getExpiration().after(new Date());
            if (!isValid) {
                throw new JwtException("Token is expired");
            }
            return true;
        } catch (Exception e) {
            throw new JwtException("Invalid token claims", e);
        }
    }

    @Override
    public String decodePassword(String encodedPassword) {
        return new String(Base64.getDecoder().decode(encodedPassword));
    }

    @Override
    public Optional<AccessToken> findByToken(String token) {
        return accessTokenRepository.findAccessTokenByToken(token);
    }

    @Override
    public void deleteByToken(String token) {
        try {
            Optional<AccessToken> accessTokenOptional = accessTokenRepository.findAccessTokenByToken(token);
            if (accessTokenOptional.isPresent()) {
                accessTokenRepository.deleteAccessTokenByToken(token);
            } else {
                throw new RuntimeException("No accessToken found for token: " + token);
            }
        } catch (Exception e) {
            System.out.println("Error deleting accessToken for token: {}" + token + e.getMessage());
            throw new RuntimeException("Error deleting accessToken for token: " + token + e.getMessage());
        }
    }

    @Override
    public void saveToken(AccessToken accessToken) {
        try {
            accessTokenRepository.save(accessToken);
        } catch (Exception e) {
            throw new RuntimeException("Error saving accessToken: " + accessToken, e);
        }
    }

    public final Claims parseJwtClaims(String token) {
        Key secretKey = secretKeyProvider.getSecretKey();
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    @Override
    public Account parseJwtTokenToAccount(String token) {
        String accessToken = token.substring(7);
        Claims accountClaims = parseJwtClaims(accessToken);

        Account accountFinding = accountService.findByEmail(accountClaims.getSubject());
        if (accountFinding == null) {
            Account accountGgleReturn = new Account();
            String email = accountClaims.get("email", String.class);
            String name = accountClaims.get("name", String.class);
            accountGgleReturn.setUserName(name);
            accountGgleReturn.setEmail(email);
            return accountGgleReturn;
        }
        return accountFinding;
    }
}


