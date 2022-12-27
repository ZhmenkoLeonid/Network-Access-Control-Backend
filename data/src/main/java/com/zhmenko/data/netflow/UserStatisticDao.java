package com.zhmenko.data.netflow;

import com.zhmenko.data.netflow.models.device.NetflowDeviceStatistic;

public interface UserStatisticDao {
    NetflowDeviceStatistic findUserStatisticByMacAddress(String macAddress, long meanValueIntervalSecond);
}
