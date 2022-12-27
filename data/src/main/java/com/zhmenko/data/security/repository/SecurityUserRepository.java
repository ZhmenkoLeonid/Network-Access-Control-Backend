package com.zhmenko.data.security.repository;

import com.zhmenko.data.security.models.SecurityUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUserEntity, UUID> {
    Optional<SecurityUserEntity> findByUsername(String username);
}
