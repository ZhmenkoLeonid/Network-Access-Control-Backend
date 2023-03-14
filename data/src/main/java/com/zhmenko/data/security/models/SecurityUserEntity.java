package com.zhmenko.data.security.models;


import com.zhmenko.data.nac.entity.NacRoleEntity;
import com.zhmenko.data.nac.entity.NetworkResourceEntity;
import com.zhmenko.data.nac.entity.UserDeviceEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "security_user")
public class SecurityUserEntity {
  @Id
  @Column(name = "id", nullable = false)
  @NotNull
  private UUID id;

  @NotBlank
  @Size(max = 20)
  @Column(name = "username", nullable = false)
  private String username;

  @NotBlank
  @Column(name = "password", nullable = false)
  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
          name = "security_user_security_role",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<SecurityRoleEntity> securityRoles;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
          name = "security_user_nac_role",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  @EqualsAndHashCode.Exclude
  private Set<NacRoleEntity> nacRoles;

  @OneToMany(cascade = CascadeType.REMOVE,
          fetch = FetchType.EAGER,
          mappedBy = "securityUserEntity")
  private Set<UserDeviceEntity> nacUserEntities;

  public Set<Integer> getPorts() {
    return getNacRoles().stream()
            .map(NacRoleEntity::getNetworkResources)
            .filter(Objects::nonNull)
            .flatMap(networkResources -> networkResources.stream()
                    .map(NetworkResourceEntity::getResourcePort))
            .collect(Collectors.toSet());
  }

}