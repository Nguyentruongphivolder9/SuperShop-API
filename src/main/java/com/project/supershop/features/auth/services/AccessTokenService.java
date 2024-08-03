package com.project.supershop.features.auth.services;

import com.project.supershop.features.auth.domain.entities.AccessToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface AccessTokenService{
    Optional<AccessToken> findByToken(String token);
    void deleteByToken(String token);
    void saveToken(AccessToken accessToken);
}
