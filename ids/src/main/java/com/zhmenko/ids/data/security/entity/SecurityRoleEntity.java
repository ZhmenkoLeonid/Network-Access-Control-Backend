package com.zhmenko.ids.data.security.entity;

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

    @ManyToMany
    @JoinTable(
            name = "security_user_security_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<SecurityUserEntity> roleOwners;
}
