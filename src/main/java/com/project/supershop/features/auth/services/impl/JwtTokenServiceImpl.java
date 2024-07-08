package com.project.supershop.features.auth.services.impl;

import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.auth.domain.dto.response.JwtResponse;
import com.project.supershop.features.auth.domain.entities.AccessToken;
import com.project.supershop.features.auth.repositories.AccessTokenRepository;
import com.project.supershop.features.auth.services.AccessTokenService;
import com.project.supershop.features.auth.services.JwtTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class JwtTokenServiceImpl implements JwtTokenService, AccessTokenService {

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final long accessTokenValidity = 60 * 60 * 1000; // 1 hour
    private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000; // 7 days

    private final JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";
    private final AccessTokenRepository accessTokenRepository;
    public JwtTokenServiceImpl(AccessTokenRepository accessTokenRepository) {
        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        this.accessTokenRepository = accessTokenRepository;
    }

    @Override
    public JwtResponse createJwtResponse(Account account) {
        Claims claims = Jwts.claims().setSubject(account.getEmail());
        claims.put("userName", account.getUserName());
        claims.put("fullName", account.getFullName());
        claims.put("email", account.getEmail());
        claims.put("role", account.getRoleName());
        claims.put("phoneNumber", account.getPhoneNumber());
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

        // Create refresh token
        Date refreshTokenExpiry = new Date(tokenCreateTime.getTime() + refreshTokenValidity);
        String refreshToken = Jwts.builder()
                .setSubject(account.getEmail())
                .setExpiration(refreshTokenExpiry)
                .signWith(secretKey)
                .compact();

        // Create JwtResponse object and set secretKey
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setAccessToken(accessToken);
        jwtResponse.setRefreshToken(refreshToken);
        jwtResponse.setExpireRefreshToken(refreshTokenExpiry.getTime());
        jwtResponse.setExpires(tokenValidity.getTime());
        jwtResponse.setAccount(account);

        return jwtResponse;
    }


    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    @Override
    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);

            if (token != null) {
                System.out.println("Received JWT token: " + token);

                Claims claims = parseJwtClaims(token);

                String computedSignature = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getSignature();
                System.out.println("Computed signature: " + computedSignature);

                if (claims != null && validateClaims(claims)) {
                    return claims;
                } else {
                    throw new JwtException("Invalid JWT token or claims");
                }
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
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
    public boolean validateClaims(Claims claims) throws AuthenticationException {
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
    public AccessToken findByToken(String token) {
        return accessTokenRepository.findAccessTokenByToken(token);
    }

    @Override
    public void deleteByToken(String token) {
        try {
           AccessToken accessTokenOptional = accessTokenRepository.findAccessTokenByToken(token);
            if (accessTokenOptional != null) {
                accessTokenRepository.deleteAccessTokenByToken(token);
            } else {
                System.out.println("No accessToken found for token: {}"+ token);
                throw new RuntimeException("No accessToken found for token: " + token);
            }
        } catch (Exception e) {
            System.out.println("Error deleting accessToken for token: {}"+ token + e.getMessage());
            throw new RuntimeException("Error deleting accessToken for token: " + token+  e.getMessage());
        }
    }

    @Override
    public void saveToken(AccessToken accessToken) {
        try {
            accessTokenRepository.save(accessToken);
        } catch (Exception e) {
            System.out.println("Error saving accessToken: {}"+ accessToken+  e.getMessage());
            throw new RuntimeException("Error saving accessToken: " + accessToken, e);
        }
    }
}
