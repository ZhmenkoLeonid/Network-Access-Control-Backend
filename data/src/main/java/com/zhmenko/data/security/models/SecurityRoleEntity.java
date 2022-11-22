package com.zhmenko.data.security.models;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "security_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "securityRoles")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<SecurityUserEntity> roleOwners;
}
