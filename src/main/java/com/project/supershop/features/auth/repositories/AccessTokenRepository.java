package com.project.supershop.features.auth.repositories;

import com.project.supershop.features.auth.domain.entities.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer> {
    AccessToken findAccessTokenByToken(String token);
    void deleteAccessTokenByToken(String token);
}
