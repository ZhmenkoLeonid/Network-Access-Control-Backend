package com.zhmenko.data.security.repository;

import com.zhmenko.data.security.models.RefreshTokenEntity;
import com.zhmenko.data.security.models.SecurityUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    int deleteByUser(SecurityUserEntity user);
}
