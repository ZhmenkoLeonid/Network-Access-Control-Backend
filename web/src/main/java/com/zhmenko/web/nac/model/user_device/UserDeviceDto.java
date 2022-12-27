package com.zhmenko.web.nac.model.user_device;

import com.zhmenko.web.netflow.model.NetflowUserStatisticDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDeviceDto {
    private String macAddress;

    private String hostname;

    private UserDeviceBlockedInfoDto blackListInfo;

    private String ipAddress;

    private Set<UserDeviceAlertDto> alerts;

    private NetflowUserStatisticDto deviceStatistic;

    private Long endSessionTimeMillis;
}
