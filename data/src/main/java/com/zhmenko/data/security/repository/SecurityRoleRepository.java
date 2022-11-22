package com.zhmenko.data.security.repository;

import com.zhmenko.data.security.models.SecurityRoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityRoleRepository extends CrudRepository<SecurityRoleEntity, Long> {
    Optional<SecurityRoleEntity> findByName(String securityRoleName);
}
