package com.zhmenko.ids.data.netflow.clickhouse.jdbc;

import com.zhmenko.ids.models.ids.packet.NetflowPacket;

import java.sql.SQLException;
import java.util.List;

public interface NetflowDao {
    List<NetflowPacket> findByIp(String ipAddress) throws SQLException;

    void deleteUserFlowsByMacAddress(String macAddress);

    void save(NetflowPacket packet, String macAddress);

    void saveList(List<NetflowPacket> packets, String macAddress);

    List<NetflowPacket> findAll();
}
