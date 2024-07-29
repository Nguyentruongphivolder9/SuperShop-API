package com.project.supershop.features.auth.providers;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class SecretKeyProvider {

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        generateNewKey();
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void generateNewKey() {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
}
