package com.zhmenko.web.security.services.impl;

import com.zhmenko.ids.data.security.repository.SecurityUserRepository;
import com.zhmenko.ids.models.ids.exception.UserNotExistException;
import com.zhmenko.web.security.data.entity.RefreshTokenEntity;
import com.zhmenko.web.security.data.repository.RefreshTokenRepository;
import com.zhmenko.web.security.exceptions.TokenRefreshException;
import com.zhmenko.web.security.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${spring.security.jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private SecurityUserRepository userRepository;

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshTokenEntity createRefreshToken(UUID userId) {
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();

        refreshToken.setUser(userRepository.findById(userId).orElseThrow(
                () -> new UserNotExistException(userId)
        ));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(UUID userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).orElseThrow(
                () -> new UserNotExistException(userId)
        ));
    }
}
