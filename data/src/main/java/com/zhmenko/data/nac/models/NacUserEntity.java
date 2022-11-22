package com.zhmenko.data.nac.models;

import com.zhmenko.data.security.models.SecurityUserEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "nac_user")
@Builder
public class NacUserEntity {
    @Id
    @Column(name = "mac_address")
    private String macAddress;
    @Column(name = "hostname")
    private String hostname;

    @OneToOne(mappedBy = "nacUserEntity", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private BlackListEntity blackListInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "security_user_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private SecurityUserEntity securityUserEntity;

    @Column(name = "ip_address")
    private String ipAddress;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "nacUserEntity")
    private Set<NacUserAlertEntity> alerts;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "nac_user_nac_role",
            joinColumns = @JoinColumn(name = "mac_address"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @EqualsAndHashCode.Exclude
    private Set<NacRoleEntity> userNacRoleEntities;


    public List<Integer> getPorts() {
        return getUserNacRoleEntities().stream()
                .map(NacRoleEntity::getNetworkResources)
                .filter(Objects::nonNull)
                .flatMap(networkResources -> networkResources.stream()
                        .map(NetworkResourceEntity::getResourcePort))
                .distinct()
                .collect(Collectors.toList());
    }
}