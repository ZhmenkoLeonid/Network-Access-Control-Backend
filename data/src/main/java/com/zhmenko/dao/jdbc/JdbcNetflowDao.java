package com.zhmenko.dao.jdbc;

import com.zhmenko.model.netflow.NetflowPacket;
import com.zhmenko.model.netflow.NetflowPacketV5;
import com.zhmenko.model.netflow.Protocol;
import com.zhmenko.model.user.UserStatistic;
import com.zhmenko.dao.NetflowDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JdbcNetflowDao implements NetflowDao {
    private static String FIND_FLOWS_BY_IP = "SELECT * from USER_FLOW_DATA where SOURCE_IP_ADDRESS=?";

    private static String DELETE_USER_FLOWS_BY_IP = "ALTER TABLE USER_FLOW_DATA DELETE WHERE SOURCE_IP_ADDRESS=?";

    private static String FIND_USER_STATISTIC_BY_IP_ADDRESS =
            "SELECT * FROM USER_STATISTIC WHERE SOURCE_IP_ADDRESS=?";

    private static String INSERT_NETFLOW_PACKET = "INSERT INTO USER_FLOW_DATA (HOSTNAME, NETFLOW_VERSION, " +
            "SOURCE_IP_ADDRESS, DESTINATION_IP_ADDRESS, SOURCE_PORT, DESTINATION_PORT, " +
            "PROTOCOL_TYPE, TIMESTAMP, TCP_FLAGS) values (?,?,?,?,?,?,?,?,?)";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<NetflowPacket> findByIp(String ipAddress) throws SQLException {
        return jdbcTemplate.query(FIND_FLOWS_BY_IP,
                (rs, rowNum) -> {
                    NetflowPacketV5 packet = new NetflowPacketV5();
                    packet.setHostname(rs.getString(1));
                    packet.setSrcIpAddress(rs.getString(3));
                    packet.setDstIpAddress(rs.getString(4));
                    packet.setSrcPort(rs.getInt(5));
                    packet.setDstPort(rs.getInt(6));
                    packet.setProtocol(Protocol.fromStringName(rs.getString(7)));
                    packet.setTimestamp(new Date(rs.getLong(8) * 1000));
                    packet.setTcpFlags(rs.getString(9));
                    return packet;
                },
                ipAddress);
    }

    @Override
    public UserStatistic findUserStatisticByIpAddress(String ipAddress) throws SQLException {
        return jdbcTemplate.queryForObject(FIND_USER_STATISTIC_BY_IP_ADDRESS,
                (rs, rowNum) -> {
                    UserStatistic userStatistic = new UserStatistic();

                    userStatistic.setIpAddress(ipAddress);

                    Map<Protocol, Integer> packetCountMap = new HashMap<>();
                    packetCountMap.put(Protocol.TCP, rs.getInt(2));
                    packetCountMap.put(Protocol.UDP, rs.getInt(3));
                    packetCountMap.put(Protocol.IGMP, rs.getInt(4));
                    packetCountMap.put(Protocol.ICMP, rs.getInt(5));
                    userStatistic.setProtocolPacketCountMap(packetCountMap);

                    Map<Protocol, Integer> uniqueDstPortCountMap = new HashMap<>();
                    uniqueDstPortCountMap.put(Protocol.TCP, rs.getInt(6));
                    uniqueDstPortCountMap.put(Protocol.UDP, rs.getInt(7));
                    uniqueDstPortCountMap.put(Protocol.ICMP, rs.getInt(8));
                    uniqueDstPortCountMap.put(Protocol.IGMP, rs.getInt(9));
                    userStatistic
                            .setProtocolUniqueDestinationPortCountMap(uniqueDstPortCountMap);

                    userStatistic.setOldestPacketTime(rs.getDate(10));

                    return userStatistic;
                },
                ipAddress);
    }

    @Override
    public void deleteUserFlowsByIp(String ipAddress) {
        jdbcTemplate.update(DELETE_USER_FLOWS_BY_IP, ipAddress);
    }

    @Override
    public void save(NetflowPacket packet) {
        jdbcTemplate.update(INSERT_NETFLOW_PACKET,
                packet.getHostname(),
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
    public void saveList(List<NetflowPacket> packets) {
        if (packets.size() == 0) return;
        jdbcTemplate.batchUpdate(INSERT_NETFLOW_PACKET, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                NetflowPacket packet = packets.get(i);
                int idx = 0;
                ps.setString(++idx, packet.getHostname());
                ps.setInt(++idx, packet.getVersion());
                ps.setString(++idx, packet.getSrcIpAddress());
                ps.setString(++idx, packet.getDstIpAddress());
                ps.setInt(++idx, packet.getSrcPort());
                ps.setInt(++idx, packet.getDstPort());
                ps.setString(++idx, packet.getProtocol().name());
                ps.setLong(++idx, packet.getTimestamp().getTime() / 1000);
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
