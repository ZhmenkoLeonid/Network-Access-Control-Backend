package com.zhmenko.web.services;

import com.zhmenko.ids.model.netflow.user.NetflowUserStatisticDto;

import java.util.Map;

public interface NetflowUserService {
    Map<String, NetflowUserStatisticDto> getAll();

    NetflowUserStatisticDto getUserStatisticByMacAddress(String macAddress);
}
