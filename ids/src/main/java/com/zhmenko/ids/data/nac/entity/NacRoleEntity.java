package com.zhmenko.ids.data.nac.entity;

import com.zhmenko.ids.data.security.entity.SecurityUserEntity;
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
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "security_user_nac_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<SecurityUserEntity> roleOwners;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "nac_role_network_resource",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_port"))
    @EqualsAndHashCode.Exclude
    private Set<NetworkResourceEntity> networkResources;
}
