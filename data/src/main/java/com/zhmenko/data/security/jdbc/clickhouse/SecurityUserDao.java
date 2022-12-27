package com.zhmenko.data.security.jdbc.clickhouse;

import com.zhmenko.data.security.models.SecurityUserEntity;

import java.util.UUID;

public interface SecurityUserDao {
    SecurityUserEntity findByUUID(UUID uuid);

    SecurityUserEntity findByUsername(String username);

    void save(SecurityUserEntity securityUser);
}
