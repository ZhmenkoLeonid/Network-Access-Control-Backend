package com.zhmenko.data.netflow;

import com.zhmenko.data.netflow.models.packet.NetflowPacket;

import java.sql.SQLException;
import java.util.List;

public interface NetflowDao {
    List<NetflowPacket> findByIp(String ipAddress) throws SQLException;

    void deleteUserFlowsByMacAddress(String macAddress);

    void save(NetflowPacket packet, String macAddress);

    void saveList(List<NetflowPacket> packets, String macAddress);

    List<NetflowPacket> findAll();
}
