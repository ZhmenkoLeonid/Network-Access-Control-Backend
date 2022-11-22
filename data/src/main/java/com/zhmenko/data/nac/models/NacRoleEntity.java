package com.zhmenko.data.nac.models;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "nac_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NacRoleEntity {
    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "userNacRoleEntities", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<NacUserEntity> roleOwners;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "nac_role_network_resource",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_port"))
    @EqualsAndHashCode.Exclude
    private Set<NetworkResourceEntity> networkResources;
}
