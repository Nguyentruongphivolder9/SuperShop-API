package com.project.supershop.features.auth.services;

import com.project.supershop.features.auth.domain.entities.AccessToken;
import org.springframework.stereotype.Component;

@Component
public interface AccessTokenService {
    AccessToken findByToken(String token);
    void deleteByToken(String token);
    void saveToken(AccessToken accessToken);
}
