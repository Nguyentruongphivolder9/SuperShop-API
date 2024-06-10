package com.project.supershop.auth.JwtTokenService;

import com.project.supershop.account.domain.entities.Account;
import com.project.supershop.config.SecurityConfig;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenService {

    private final String secret_key = "SUPERSHOPSECRETKEYSUPERHARDTOGUEST";
    private long accessTokenValidity = 60*60*1000;

    private final JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    public JwtTokenService(){
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }

    public String createToken(Account account) {
        Claims claims = Jwts.claims().setSubject(account.getEmail());
        claims.put("userName",account.getUserName());
        claims.put("fullName",account.getFullName());
        claims.put("email", account.getEmail());
        claims.put("role", account.getRoleName());
        claims.put("phoneNumber", account.getPhoneNumber());
        claims.put("password", decodePassword(account.getPassword()));
        claims.put("gender", account.getGender());
        claims.put("avatarUrl", account.getAvatarUrl());
        claims.put("isActive", account.getIsActive());
        //Default Type: LocalDateTime
//        claims.put("birthDay", account.getBirthDay().toString());
//        claims.put("updatedAt", account.getUpdatedAt().toString());
//        claims.put("createdAt", account.getCreatedAt().toString());
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

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

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    public String decodePassword(String encodedPassword){
        return new String(Base64.getDecoder().decode(encodedPassword));
    }

}
