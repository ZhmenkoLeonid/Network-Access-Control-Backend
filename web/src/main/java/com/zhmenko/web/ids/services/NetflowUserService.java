package com.zhmenko.web.ids.services;

import com.zhmenko.data.netflow.models.user.NetflowUserStatisticDto;

import java.util.Map;

public interface NetflowUserService {
    Map<String, NetflowUserStatisticDto> getAll();

    NetflowUserStatisticDto getUserStatisticByMacAddress(String macAddress);
}
