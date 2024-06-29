package com.project.supershop.features.auth.services.impl;

import com.project.supershop.features.account.domain.entities.Account;
import com.project.supershop.features.auth.dto.response.JwtResponse;
import com.project.supershop.features.auth.services.JwtTokenService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Date;

@Service
@Transactional
public class JwtTokenServiceImpl implements JwtTokenService {
    //    @Value("${TOKEN_SECRET_KEY}")
    private final String secretKey = "SUPERSHOPSECRETKEYSUPERHARDTOGUEST";

    private final long accessTokenValidity = 60 * 60 * 1000; // 1 hour
    private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000; // 7 days

    private final JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer";

    public JwtTokenServiceImpl() {
        this.jwtParser = Jwts.parser().setSigningKey(secretKey);
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
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Create refresh token
        Date refreshTokenExpiry = new Date(tokenCreateTime.getTime() + refreshTokenValidity);
        String refreshToken = Jwts.builder()
                .setSubject(account.getEmail())
                .setExpiration(refreshTokenExpiry)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Create JwtResponse object and return
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
                return parseJwtClaims(token);
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

    @Override
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
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public String decodePassword(String encodedPassword) {
        return new String(Base64.getDecoder().decode(encodedPassword));
    }
}