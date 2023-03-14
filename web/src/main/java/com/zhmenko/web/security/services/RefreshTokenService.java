package com.zhmenko.web.security.services;

import com.zhmenko.data.security.models.RefreshTokenEntity;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {
    Optional<RefreshTokenEntity> findByToken(String token);

    RefreshTokenEntity createRefreshToken(UUID userId);
    RefreshTokenEntity verifyExpiration(RefreshTokenEntity token);
    int deleteByUserId(UUID userId);
}
