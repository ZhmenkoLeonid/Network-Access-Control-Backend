package com.zhmenko.entity;

import com.zhmenko.ids.data.nac.entity.NacRoleEntity;
import com.zhmenko.ids.data.nac.entity.UserDeviceEntity;
import com.zhmenko.ids.data.security.entity.SecurityRoleEntity;
import com.zhmenko.ids.data.security.entity.SecurityUserEntity;
import com.zhmenko.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aSecurityUserEntity")
@With
public class SecurityUserEntityTestBuilder implements TestBuilder<SecurityUserEntity> {
    private UUID id = UUID.randomUUID();
    private String username = "user";
    private String password = "password";
    private Set<SecurityRoleEntity> securityRoles = new HashSet<>();
    private Set<NacRoleEntity> nacRoles = new HashSet<>();
    private Set<UserDeviceEntity> nacUserEntities = new HashSet<>();

    @Override
    public SecurityUserEntity build() {
        final var server = new SecurityUserEntity();
        server.setId(id);
        server.setUsername(username);
        server.setPassword(password);
        server.setSecurityRoles(securityRoles);
        server.setNacRoles(nacRoles);
        server.setNacUserEntities(nacUserEntities);
        return server;
    }
}
