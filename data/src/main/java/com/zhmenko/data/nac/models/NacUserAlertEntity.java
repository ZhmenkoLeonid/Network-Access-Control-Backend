package com.zhmenko.data.nac.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "nac_user_alert", schema = "public", catalog = "postgres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NacUserAlertEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "message_id")
    private int messageId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mac_address", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private NacUserEntity nacUserEntity;

    @Column(name = "alert_message")
    private String alertMessage;
}
