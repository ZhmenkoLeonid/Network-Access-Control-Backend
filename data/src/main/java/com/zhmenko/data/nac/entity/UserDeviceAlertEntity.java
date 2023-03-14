package com.zhmenko.data.nac.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user_device_alerts", schema = "public", catalog = "postgres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeviceAlertEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "message_id", nullable = false)
    private int messageId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mac_address", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserDeviceEntity userDeviceEntity;

    @Column(name = "alert_message")
    private String alertMessage;
}
