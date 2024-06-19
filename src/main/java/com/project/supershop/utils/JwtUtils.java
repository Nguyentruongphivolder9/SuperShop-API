package com.project.supershop.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class JwtUtils {
    public static Claims decodeJwt(String jwt, String secretKey){
        try{
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwt)
                    .getBody();
        }catch(SignatureException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
