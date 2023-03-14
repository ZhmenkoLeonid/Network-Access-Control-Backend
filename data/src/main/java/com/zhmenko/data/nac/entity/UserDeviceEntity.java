package com.zhmenko.data.nac.entity;

import com.zhmenko.data.security.models.SecurityUserEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_device")
@Builder
public class UserDeviceEntity {
    @Id
    @Column(name = "mac_address", nullable = false)
    @NotNull
    private String macAddress;

    @Column(name = "hostname")
    private String hostname;

    @OneToOne(mappedBy = "userDeviceEntity", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @NotNull
    private UserBlockInfoEntity blackListInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "security_user_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private SecurityUserEntity securityUserEntity;

    @Column(name = "ip_address")
    private String ipAddress;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "userDeviceEntity")
    private Set<UserDeviceAlertEntity> alerts;
}