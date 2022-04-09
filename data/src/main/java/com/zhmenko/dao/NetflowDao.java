package com.zhmenko.dao;

import com.zhmenko.model.netflow.NetflowPacket;
import com.zhmenko.model.user.UserStatistic;

import java.sql.SQLException;
import java.util.List;

public interface NetflowDao {
    public List<NetflowPacket> findByIp(String ipAddress) throws SQLException;

    public UserStatistic findUserStatisticByIpAddress(String ipAddress) throws SQLException;

    public void deleteUserFlowsByIp(String ipAddress);

    public void save(NetflowPacket packet);

    public void saveList(List<NetflowPacket> packets);

    public List<NetflowPacket> findAll();
}
