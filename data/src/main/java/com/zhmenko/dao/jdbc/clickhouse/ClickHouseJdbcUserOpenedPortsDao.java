/*
package com.zhmenko.dao.jdbc.clickhouse;

import com.zhmenko.dao.UserOpenedPortsDao;
import com.zhmenko.security.security.request.UserPortsRequest;
import com.zhmenko.security.security.response.UserPortsResponse;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@AllArgsConstructor
public class ClickHouseJdbcUserOpenedPortsDao implements UserOpenedPortsDao {
    private final String INSERT_USER_PORT = "INSERT INTO USER_OPENED_PORT (MAC_ADDRESS, IP_ADDRESS, PORT) values (?,?,?)";
    private final String DELETE_USER_PORT = "ALTER TABLE USER_OPENED_PORT DELETE WHERE MAC_ADDRESS=? AND IP_ADDRESS=? AND PORT=?";
    private final String DELETE_USER_PORTS_BY_MAC = "ALTER TABLE USER_OPENED_PORT DELETE WHERE MAC_ADDRESS=?";
    private final String SELECT_PORTS_BY_MAC_ADDRESS = "SELECT IP_ADDRESS, PORT FROM USER_OPENED_PORT WHERE MAC_ADDRESS=?";
    private final String SELECT_PORTS_BY_IP_ADDRESS = "SELECT PORT FROM USER_OPENED_PORT WHERE IP_ADDRESS=?";
    private final String SELECT_ALL_PORTS = "SELECT * FROM USER_OPENED_PORT";

    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(UserPortsRequest userPortsDto) {
        List<Integer> ports = userPortsDto.getPorts();
        if (ports.size() == 0) return;
        String macAddress = userPortsDto.getMacAddress();
        String userIp = userPortsDto.getIpAddress();
        jdbcTemplate.batchUpdate(INSERT_USER_PORT, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1,macAddress);
                ps.setString(2, userIp);
                ps.setInt(3, ports.get(i));
            }

            @Override
            public int getBatchSize() {
                return ports.size();
            }
        });
    }

    @Override
    public void removeUserPorts(UserPortsRequest userPortsRequest) {
        List<Integer> ports = userPortsRequest.getPorts();
        if (ports.size() == 0) return;
        String macAddress = userPortsRequest.getMacAddress();
        String userIp = userPortsRequest.getIpAddress();
        jdbcTemplate.batchUpdate(DELETE_USER_PORT, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1,macAddress);
                ps.setString(2, userIp);
                ps.setInt(3, ports.get(i));
            }

            @Override
            public int getBatchSize() {
                return ports.size();
            }
        });
    }

    @Override
    public List<UserPortsResponse> findUsersPorts() {
        Map<String, UserPortsResponse> usersPortsMap = new HashMap<>();
        jdbcTemplate.query(SELECT_ALL_PORTS,
                (rs, rowNum) -> {
                    int idx = 0;
                    String macAddress = rs.getString(++idx);
                    String ipAddress = rs.getString(++idx);
                    int port = rs.getInt(++idx);
                    // Достаём мапу ip->{ports}
                    usersPortsMap.putIfAbsent(macAddress,new UserPortsResponse(macAddress));
                    Map<String,List<Integer>> userIpPortsMap = usersPortsMap.get(macAddress).getIpAddressPortsMap();
                    // Добавляем port в соответствующий список
                    userIpPortsMap.putIfAbsent(ipAddress,new ArrayList<>());
                    userIpPortsMap.get(ipAddress).add(port);
                    return 0;
                });
        return new ArrayList<>(usersPortsMap.values());
    }

    @Override
    public UserPortsResponse findUserPortsByMacAddress(String macAddress) {
        Map<String, List<Integer>> ipPortsMap = new HashMap<>();
        jdbcTemplate.query(SELECT_PORTS_BY_MAC_ADDRESS,
                (rs, rowNum) -> {
                    int idx = 0;
                    String ipAddress = rs.getString(++idx);
                    int port = rs.getInt(++idx);
                    ipPortsMap.putIfAbsent(ipAddress,new ArrayList<>());
                    ipPortsMap.get(ipAddress).add(port);
                    return 0;
                },
                macAddress);
        return new UserPortsResponse(macAddress,ipPortsMap);
    }

    @Override
    public List<Integer> findUserPortsByIp(String ipAddress) {
        return jdbcTemplate.query(
                SELECT_PORTS_BY_IP_ADDRESS,
                (rs, rowNum) -> rs.getInt(2),
                ipAddress);
    }

    @Override
    public void removeUserPortsByMacAddress(String macAddress) {
        jdbcTemplate.update(DELETE_USER_PORTS_BY_MAC, macAddress);
    }
}
*/
