package com.zhmenko.web.services;

import com.zhmenko.model.netflow.NetflowPacket;
import com.zhmenko.model.netflow.Protocol;
import com.zhmenko.model.user.UserStatistic;

import java.sql.SQLException;
import java.util.Map;
import java.util.List;

public interface NetflowService {
    public List<NetflowPacket> getByIp(String ipAddress) throws SQLException;

    public UserStatistic getUserStatisticByIpAddress(String ipAddress) throws SQLException;

    public void save(NetflowPacket packet);

    public void saveList(List<NetflowPacket> packets);

    public void saveProtocolListMap(Map<Protocol, List<NetflowPacket>> protocolListMap);

    public List<NetflowPacket> getAll();
}
