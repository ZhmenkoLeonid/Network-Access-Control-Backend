package com.zhmenko.web.security.data.repository;

import com.zhmenko.ids.data.security.entity.SecurityUserEntity;
import com.zhmenko.web.security.data.entity.RefreshTokenEntity;
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
