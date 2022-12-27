package com.zhmenko.data.nac.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_device_block_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBlockInfoEntity {
    @Id
    @Column(name = "mac_address", nullable = false)
    @NotNull
    private String macAddress;

    @Column(name = "user_banned")
    private Boolean isBlocked;

    @Column(name = "last_ban_timestamp")
    private OffsetDateTime whenBlocked;

    @Column(name = "last_unban_timestamp")
    private OffsetDateTime whenUnblocked;

    @OneToOne
    @MapsId
    @JoinColumn(name = "mac_address")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @NotNull
    private UserDeviceEntity userDeviceEntity;
}
