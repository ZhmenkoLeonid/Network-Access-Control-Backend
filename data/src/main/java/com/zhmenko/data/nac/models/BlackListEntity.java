package com.zhmenko.data.nac.models;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "nac_user_blacklist")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlackListEntity {
    @Id
    @Column(name = "mac_address")
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
    private NacUserEntity nacUserEntity;
}
