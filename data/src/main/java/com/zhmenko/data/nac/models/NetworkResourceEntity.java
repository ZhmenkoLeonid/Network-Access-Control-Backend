package com.zhmenko.data.nac.models;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "network_resource")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkResourceEntity {
    @Id
    @Column(name = "resource_port")
    private int resourcePort;

    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinTable(name = "nac_role_network_resource",
            joinColumns = @JoinColumn(name = "resource_port"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<NacRoleEntity> nacRoleEntities;
}
