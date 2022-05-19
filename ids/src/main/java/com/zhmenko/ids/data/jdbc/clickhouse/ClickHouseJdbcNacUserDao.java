package com.zhmenko.ids.data.jdbc.clickhouse;

import com.zhmenko.ids.data.NacUserDao;
import com.zhmenko.ids.mapper.DBArrayMapper;
import com.zhmenko.ids.model.nac.NacUserDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.util.*;

@Repository
@AllArgsConstructor
@Slf4j
public class ClickHouseJdbcNacUserDao implements NacUserDao {
    private final String SELECT_USER_BY_MAC_ADDRESS = "SELECT * FROM NAC_USER WHERE MAC_ADDRESS=?";
    private final String SELECT_ALL_USERS = "SELECT * FROM NAC_USER";
    private final String SELECT_PORTS_BY_MAC_ADDRESS = "SELECT PORTS FROM NAC_USER WHERE MAC_ADDRESS=?";
    private final String SELECT_ALERTS_BY_MAC_ADDRESS = "SELECT ALERTS FROM NAC_USER WHERE MAC_ADDRESS=?";

    private final String UPDATE_USER = "ALTER TABLE NAC_USER UPDATE HOSTNAME=?, IS_BLACKLISTED=?, IP_ADDRESS=?, PORTS=? WHERE MAC_ADDRESS=?";
    private final String UPDATE_PORTS_BY_MAC_ADDRESS = "ALTER TABLE NAC_USER UPDATE PORTS = ? WHERE MAC_ADDRESS=?";
    private final String UPDATE_ALERTS_BY_MAC_ADDRESS = "ALTER TABLE NAC_USER UPDATE ALERTS = ? WHERE MAC_ADDRESS=?";

    private final String SAVE_USER = "Insert Into NAC_USER (MAC_ADDRESS, HOSTNAME, IS_BLACKLISTED, IP_ADDRESS, PORTS) " +
            "values (?,?,?,?,?)";

    private final String DELETE_USER_BY_MAC_ADDRESS = "ALTER TABLE NAC_USER DELETE WHERE MAC_ADDRESS=?";

    private final String IS_EXIST_BY_MAC_ADDRESS = "SELECT COUNT(*) FROM NAC_USER WHERE MAC_ADDRESS=?";

    private final JdbcTemplate jdbcTemplate;
    private final DBArrayMapper arrayMapper;

    @Override
    public NacUserDto findByMacAddress(String macAddress) {
        return jdbcTemplate.queryForObject(SELECT_USER_BY_MAC_ADDRESS,
                (rs, rowNum) -> {
                    int idx = 1;
                    String hostname = rs.getString(++idx);
                    boolean isBlacklisted = rs.getInt(++idx) == 1;
                    String ipAddress = rs.getString(++idx);
                    Array portsArray = rs.getArray(++idx);
                    List<Integer> portsList = arrayMapper.toIntegerList(portsArray);
                    Array alertsArray = rs.getArray(++idx);
                    List<String> alertsList = arrayMapper.toStringList(alertsArray);

                    return new NacUserDto(macAddress, hostname, isBlacklisted, ipAddress, portsList, alertsList);
                },
                macAddress);
    }

    @Override
    public List<Integer> findPortsByMacAddress(String macAddress) {
        return jdbcTemplate.queryForObject(SELECT_PORTS_BY_MAC_ADDRESS,
                (rs, rowNum) -> {
                    Array array = rs.getArray(1);
                    return arrayMapper.toIntegerList(array);
                },
                macAddress);
    }

    @Override
    public List<String> findAlertsByMacAddress(String macAddress) {
        return jdbcTemplate.queryForObject(SELECT_ALERTS_BY_MAC_ADDRESS,
                (rs,rowNum) -> {
                    Array array = rs.getArray(1);
                    return arrayMapper.toStringList(array);
                },
                macAddress);
    }

    @Override
    public void save(NacUserDto nacUserDto) {
        jdbcTemplate.update(SAVE_USER,
                nacUserDto.getMacAddress(),
                nacUserDto.getHostname(),
                nacUserDto.isBlacklisted() ? 1 : 0,
                nacUserDto.getIpAddress(),
                nacUserDto.getPorts().toString());
    }

    @Override
    public void update(NacUserDto nacUserDto) {
        jdbcTemplate.update(UPDATE_USER,
                nacUserDto.getHostname(),
                nacUserDto.isBlacklisted() ? 1 : 0,
                nacUserDto.getIpAddress(),
                nacUserDto.getPorts().toString(),
                nacUserDto.getMacAddress());
    }

    @Override
    public void removeByMacAddress(String macAddress) {
        jdbcTemplate.update(DELETE_USER_BY_MAC_ADDRESS, macAddress);
    }

    @Override
    public void insertAlertsByMacAddress(String macAddress, List<String> alerts) {
        Set<String> dbAlerts = new HashSet<>(findAlertsByMacAddress(macAddress));
        dbAlerts.addAll(alerts);
        jdbcTemplate.update(UPDATE_ALERTS_BY_MAC_ADDRESS, arrayMapper.fromStringCollection(dbAlerts), macAddress);
    }

    @Override
    public void removeAlertsByMacAddress(String macAddress, List<String> alerts) {
        List<String> dbAlerts = findAlertsByMacAddress(macAddress);
        dbAlerts.removeAll(alerts);
        jdbcTemplate.update(UPDATE_ALERTS_BY_MAC_ADDRESS, arrayMapper.fromStringCollection(dbAlerts), macAddress);
    }

    @Override
    public void removeAlertsByMacAddress(String macAddress) {
        jdbcTemplate.update(UPDATE_ALERTS_BY_MAC_ADDRESS, Collections.emptyList().toString(), macAddress);
    }

    @Override
    public void insertPortsByMacAddress(String macAddress, List<Integer> ports) {
        List<Integer> portsDb = findPortsByMacAddress(macAddress);
        for (Integer newPort : ports) {
            if (!portsDb.contains(newPort)) portsDb.add(newPort);
        }
/*        //Map<String, List<Integer>> ipAddressPortsMapDB = user.getIpAddressPortsMap();
        for (String ipAddress : ipAddressPortsMap.keySet()) {
            List<Integer> newPorts = ipAddressPortsMap.get(ipAddress);
            ipAddressPortsMapDB.putIfAbsent(ipAddress, new ArrayList<>());
            List<Integer> dbPorts = ipAddressPortsMapDB.get(ipAddress);
            for (Integer newPort : newPorts) {
                if (!dbPorts.contains(newPort)) dbPorts.add(newPort);
            }
        }*/
        jdbcTemplate.update(UPDATE_PORTS_BY_MAC_ADDRESS, portsDb.toString(), macAddress);
    }


    @Override
    public void removePortsByMacAddress(String macAddress, List<Integer> ports) {
        List<Integer> portsDb = findPortsByMacAddress(macAddress);
        for (Integer newPort : ports) {
            portsDb.remove(newPort);
        }
/*        Map<String, List<Integer>> ipAddressPortsMapDB = user.getIpAddressPortsMap();
        for (String ipAddress : ipAddressPortsMap.keySet()) {
            List<Integer> newPorts = ipAddressPortsMap.get(ipAddress);
            ipAddressPortsMapDB.putIfAbsent(ipAddress, new ArrayList<>());
            List<Integer> dbPorts = ipAddressPortsMapDB.get(ipAddress);
            for (Integer newPort : newPorts) {
                dbPorts.remove(newPort);
            }
        }
        String query = UPDATE_PORTS_BY_MAC_ADDRESS_START
                + MapConverter.toDbType(ipAddressPortsMapDB)
                + UPDATE_PORTS_BY_MAC_ADDRESS_END;*/
        jdbcTemplate.update(UPDATE_PORTS_BY_MAC_ADDRESS, portsDb.toString(), macAddress);
    }

    @Override
    public void removePortsByMacAddress(String macAddress) {
        jdbcTemplate.update(UPDATE_PORTS_BY_MAC_ADDRESS, Collections.emptyList().toString(), macAddress);
    }

    @Override
    public boolean isExistByMacAddress(String macAddress) {
        int cnt = Optional.ofNullable(jdbcTemplate.queryForObject(
                IS_EXIST_BY_MAC_ADDRESS,
                Integer.class,
                macAddress)).orElse(0);
        return cnt > 0;
    }

    @Override
    public List<NacUserDto> findAll() {
        return jdbcTemplate.query(SELECT_ALL_USERS,
                (rs, rowNum) -> {
                    int idx = 0;
                    String macAddress = rs.getString(++idx);
                    String hostname = rs.getString(++idx);
                    boolean isBlacklisted = rs.getInt(++idx) == 1;
                    String ipAddress = rs.getString(++idx);
                    Array portsArray = rs.getArray(++idx);
                    List<Integer> portsList = arrayMapper.toIntegerList(portsArray);
                    Array alertsArray = rs.getArray(++idx);
                    List<String> alertsList = arrayMapper.toStringList(alertsArray);

                    return new NacUserDto(macAddress, hostname, isBlacklisted, ipAddress, portsList, alertsList);
                });
    }
}