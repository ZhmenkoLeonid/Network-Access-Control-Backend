package com.zhmenko.entity;

import com.zhmenko.ids.data.security.entity.SecurityRoleEntity;
import com.zhmenko.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aSecurityRoleEntity")
@With
public class SecurityRoleEntityTestBuilder implements TestBuilder<SecurityRoleEntity> {
    private String name = "security role";
    @Override
    public SecurityRoleEntity build() {
        final var server = new SecurityRoleEntity();
        server.setName(name);
        return server;
    }
}
