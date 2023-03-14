package com.zhmenko.entity;

import com.zhmenko.ids.data.nac.entity.NacRoleEntity;
import com.zhmenko.ids.data.nac.entity.NetworkResourceEntity;
import com.zhmenko.util.TestBuilder;

import java.util.HashSet;
import java.util.Set;

public class NetworkResourceEntityTestBuilder implements TestBuilder<NetworkResourceEntity> {
    private Integer port = 9999;
    private String name = "some resource";
    private Set<NacRoleEntity> nacRoleEntities = new HashSet<>();


    private NetworkResourceEntityTestBuilder() {
    }

    public NetworkResourceEntityTestBuilder(Integer port, String name, Set<NacRoleEntity> nacRoleEntities) {
        this.port = port;
        this.name = name;
        this.nacRoleEntities = nacRoleEntities;
    }

    public static NetworkResourceEntityTestBuilder aNetworkResourceEntity() {
        return new NetworkResourceEntityTestBuilder();
    }

    @Override
    public NetworkResourceEntity build() {
        final var server = new NetworkResourceEntity();
        server.setResourcePort(port);
        server.setName(name);
        server.setNacRoleEntities(nacRoleEntities);
        return server;
    }

    public NetworkResourceEntityTestBuilder withPort(Integer port) {
        return this.port == port ? this : new NetworkResourceEntityTestBuilder(port, this.name, this.nacRoleEntities);
    }

    public NetworkResourceEntityTestBuilder withName(String name) {
        return this.name == name ? this : new NetworkResourceEntityTestBuilder(this.port, name, this.nacRoleEntities);
    }

    public NetworkResourceEntityTestBuilder withNacRoleEntities(Set<NacRoleEntity> nacRoleEntities) {
        return this.nacRoleEntities == nacRoleEntities ? this : new NetworkResourceEntityTestBuilder(this.port, this.name, nacRoleEntities);
    }
}
