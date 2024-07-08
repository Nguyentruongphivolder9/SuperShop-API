package com.project.supershop.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;

public class JwtUtils {
    public static Claims extractClaims(String jwtToken, String secretKey) {
        try {
            // Giải mã phần payload của JWT token
            String[] parts = jwtToken.split("\\.");
            String encodedPayload = parts[1];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedPayload);
            String decodedPayload = new String(decodedBytes, "UTF-8");

            // Parse và trả về các claims từ payload đã giải mã
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Could not extract claims from JWT token", e);
        }
    }
}
