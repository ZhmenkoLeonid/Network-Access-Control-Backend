package com.zhmenko.web.ids.services;

import com.zhmenko.web.netflow.model.NetflowUserStatisticDto;

import java.util.Map;

public interface NetflowUserStatisticService {
    Map<String, NetflowUserStatisticDto> getAll();

    NetflowUserStatisticDto getUserStatisticByMacAddress(String macAddress);
}
