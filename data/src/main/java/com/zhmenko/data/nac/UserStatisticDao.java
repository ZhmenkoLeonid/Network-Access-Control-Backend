package com.zhmenko.data.nac;

import com.zhmenko.data.netflow.models.user.NetflowUserStatistic;

public interface UserStatisticDao {
    NetflowUserStatistic findUserStatisticByMacAddress(String macAddress, long meanValueIntervalSecond);
}
