package com.zhmenko.web.security.data.repository;

import com.zhmenko.ids.data.security.entity.SecurityRoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityRoleRepository extends CrudRepository<SecurityRoleEntity, Long> {
    Optional<SecurityRoleEntity> findByName(String securityRoleName);
}
