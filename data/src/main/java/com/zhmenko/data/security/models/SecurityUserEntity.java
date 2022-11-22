package com.zhmenko.data.security.models;


import com.zhmenko.data.nac.models.NacUserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "security_user")
@Builder
public class SecurityUserEntity {
  @Id
  private UUID id;

  @NotBlank
  @Size(max = 20)
  @Column
  private String username;

  @NotBlank
  @Column
  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
          name = "security_user_security_role",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<SecurityRoleEntity> securityRoles;

  @OneToMany(cascade = CascadeType.ALL,
          fetch = FetchType.EAGER,
          mappedBy = "securityUserEntity")
  private Set<NacUserEntity> nacUserEntities;
}