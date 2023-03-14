package com.zhmenko.ids.data.nac.repository;

import com.zhmenko.ids.data.nac.entity.UserDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDeviceEntity, String> {
    Optional<UserDeviceEntity> findByMacAddress(String macAddress);

    void removeByMacAddress(String macAddress);

    boolean existsByIpAddress(String ipAddress);

    Optional<UserDeviceEntity> findByIpAddress(String ipAddress);

    List<UserDeviceEntity> findAllByIpAddress(String ipAddress);
}
