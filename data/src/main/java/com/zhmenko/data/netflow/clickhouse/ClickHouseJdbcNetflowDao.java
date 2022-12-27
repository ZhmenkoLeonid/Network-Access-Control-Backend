package com.zhmenko.data.netflow.clickhouse;

import com.zhmenko.data.netflow.NetflowDao;
import com.zhmenko.data.netflow.models.Protocol;
import com.zhmenko.data.netflow.models.packet.NetflowPacket;
import com.zhmenko.data.netflow.models.packet.NetflowPacketV5;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@AllArgsConstructor
public class ClickHouseJdbcNetflowDao implements NetflowDao {
    private final String FIND_FLOWS_BY_IP = "SELECT * from USER_FLOW_DATA where SOURCE_IP_ADDRESS=?";

    private final String DELETE_USER_FLOWS_BY_IP = "ALTER TABLE USER_FLOW_DATA DELETE WHERE SOURCE_IP_ADDRESS=?";

    private final String DELETE_USER_FLOWS_BY_MAC_ADDRESS= "ALTER TABLE USER_FLOW_DATA DELETE WHERE MAC_ADDRESS=?";

    private final String FIND_USER_STATISTIC_BY_MAC_ADDRESS = "SELECT * FROM USER_STATISTIC WHERE MAC_ADDRESS=?";

    private final String INSERT_NETFLOW_PACKET = "INSERT INTO USER_FLOW_DATA (MAC_ADDRESS, HOSTNAME, NETFLOW_VERSION," +
            " SOURCE_IP_ADDRESS, DESTINATION_IP_ADDRESS, SOURCE_PORT, DESTINATION_PORT, PROTOCOL_TYPE, TIMESTAMP, TCP_FLAGS) " +
            "values (?,?,?,?,?,?,?,?,?,?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<NetflowPacket> findByIp(String ipAddress) {
        return jdbcTemplate.query(FIND_FLOWS_BY_IP,
                (rs, rowNum) -> {
                    int idx = 0;
                    NetflowPacketV5 packet = new NetflowPacketV5();
                    // TODO убрать hostname из таблицы
                    idx++;
                    packet.setSrcIpAddress(rs.getString(++idx));
                    packet.setDstIpAddress(rs.getString(++idx));
                    packet.setSrcPort(rs.getInt(++idx));
                    packet.setDstPort(rs.getInt(++idx));
                    packet.setProtocol(Protocol.fromStringName(rs.getString(++idx)));
                    packet.setTimestamp(rs.getTimestamp(++idx));
                    packet.setTcpFlags(rs.getString(++idx));
                    return packet;
                },
                ipAddress);
    }


    @Override
    public void deleteUserFlowsByMacAddress(String macAddress) {
        jdbcTemplate.update(DELETE_USER_FLOWS_BY_MAC_ADDRESS, macAddress);
    }

    @Override
    public void save(NetflowPacket packet, String macAddress) {
        jdbcTemplate.update(INSERT_NETFLOW_PACKET,
                macAddress,
                "",
                packet.getVersion(),
                packet.getSrcIpAddress(),
                packet.getDstIpAddress(),
                packet.getSrcPort(),
                packet.getDstPort(),
                packet.getProtocol().name(),
                packet.getTimestamp().getTime() / 1000,
                packet.getTcpFlags());
    }

    @Override
    public void saveList(List<NetflowPacket> packets, String macAddress) {
        if (packets.size() == 0) return;
        jdbcTemplate.batchUpdate(INSERT_NETFLOW_PACKET, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                NetflowPacket packet = packets.get(i);
                int idx = 0;
                ps.setString(++idx, macAddress);
                ps.setString(++idx, "");
                ps.setInt(++idx, packet.getVersion());
                ps.setString(++idx, packet.getSrcIpAddress());
                ps.setString(++idx, packet.getDstIpAddress());
                ps.setInt(++idx, packet.getSrcPort());
                ps.setInt(++idx, packet.getDstPort());
                ps.setString(++idx, packet.getProtocol().name());
                ps.setTimestamp(++idx, packet.getTimestamp());
                ps.setString(++idx, packet.getTcpFlags());
            }

            @Override
            public int getBatchSize() {
                return packets.size();
            }
        });
    }

    @Override
    public List<NetflowPacket> findAll() {
        return null;
    }
}
