package com.zhmenko.data.nac.repository;


import com.zhmenko.data.nac.entity.NacRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NacRoleRepository extends JpaRepository<NacRoleEntity, Long> {
    boolean existsByName(String roleName);

    @Modifying
    void deleteByName(String roleName);

    Optional<NacRoleEntity> findByName(String roleName);
}
