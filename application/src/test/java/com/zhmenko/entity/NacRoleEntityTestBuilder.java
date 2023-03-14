package com.zhmenko.entity;

import com.zhmenko.ids.data.nac.entity.NacRoleEntity;
import com.zhmenko.ids.data.nac.entity.NetworkResourceEntity;
import com.zhmenko.ids.data.security.entity.SecurityUserEntity;
import com.zhmenko.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aNacRoleEntity")
@With
public class NacRoleEntityTestBuilder implements TestBuilder<NacRoleEntity> {
    //private Long id = 1L;
    private String name = "role name";
    private Set<SecurityUserEntity> securityUserEntities = new HashSet<>();
    private Set<NetworkResourceEntity> networkResourceEntities = new HashSet<>();
    @Override
    public NacRoleEntity build() {
        final var server = new NacRoleEntity();
        //server.setId(id);
        server.setName(name);
        server.setRoleOwners(securityUserEntities);
        server.setNetworkResources(networkResourceEntities);
        return server;
    }
}
