package com.zhmenko.web.netflow.model;

import com.zhmenko.ids.models.ids.Protocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetflowUserStatisticDto {
    private String macAddress;
    private long meanPacketCount;
    private long meanLastPeriodPacketCount;
    private long periodMillis;
    private Map<Protocol, Long> uniqueDestinationPortsProtocolsCountMap;
}
