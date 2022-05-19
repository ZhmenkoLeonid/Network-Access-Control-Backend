package com.zhmenko.ids.data;


import com.zhmenko.ids.model.netflow.user.NetflowUserStatistic;

public interface UserStatisticDao {
    NetflowUserStatistic findUserStatisticByMacAddress(String macAddress, long meanValueIntervalSecond);
}
