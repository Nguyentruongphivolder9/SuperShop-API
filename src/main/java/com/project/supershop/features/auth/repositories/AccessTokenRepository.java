package com.project.supershop.features.auth.repositories;

import com.project.supershop.features.auth.domain.entities.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccessTokenRepository extends JpaRepository<AccessToken, UUID> {
    Optional<AccessToken> findAccessTokenByToken(String token);
    void deleteAccessTokenByToken(String token);
}
