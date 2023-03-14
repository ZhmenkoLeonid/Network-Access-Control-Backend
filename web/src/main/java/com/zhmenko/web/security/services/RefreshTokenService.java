package com.zhmenko.web.security.services;

import com.zhmenko.web.security.data.entity.RefreshTokenEntity;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {
    Optional<RefreshTokenEntity> findByToken(String token);

    RefreshTokenEntity createRefreshToken(UUID userId);
    RefreshTokenEntity verifyExpiration(RefreshTokenEntity token);
    int deleteByUserId(UUID userId);
}
