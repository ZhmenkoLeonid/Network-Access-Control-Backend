package com.zhmenko.data.nac.repository;

import com.zhmenko.data.nac.models.NacUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NacUserRepository extends JpaRepository<NacUserEntity, String> {
    NacUserEntity findByMacAddress(String macAddress);

    @Modifying
    void removeByMacAddress(String macAddress);

    boolean existsByIpAddress(String ipAddress);

    Optional<NacUserEntity> findByIpAddress(String ipAddress);
}
