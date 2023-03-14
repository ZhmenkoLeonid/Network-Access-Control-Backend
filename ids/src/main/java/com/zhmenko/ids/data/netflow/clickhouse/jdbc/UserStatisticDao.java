package com.zhmenko.ids.data.netflow.clickhouse.jdbc;

import com.zhmenko.ids.models.ids.device.NetflowDeviceStatistic;

public interface UserStatisticDao {
    NetflowDeviceStatistic findUserStatisticByMacAddress(String macAddress, long meanValueIntervalSecond);
}
