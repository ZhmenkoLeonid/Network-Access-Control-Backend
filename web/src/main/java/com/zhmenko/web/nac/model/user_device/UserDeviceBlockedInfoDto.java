package com.zhmenko.web.nac.model.user_device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeviceBlockedInfoDto {
    private String macAddress;

    private boolean isBlocked;

    private OffsetDateTime whenBlocked;

    private OffsetDateTime whenUnblocked;
}
