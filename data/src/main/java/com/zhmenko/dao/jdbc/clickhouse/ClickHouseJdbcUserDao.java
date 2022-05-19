/*
package com.zhmenko.dao.jdbc.clickhouse;

import com.zhmenko.dao.UserDao;
import com.zhmenko.ips.model.netflow.user.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ClickHouseJdbcUserDao implements UserDao {
    private final String SELECT_ALL_USERS = "SELECT MAC_ADDRESS,HOSTNAME FROM USER";
    private final String SELECT_USER_BY_MAC = "SELECT * FROM USER WHERE ID=?";
    private final String COUNT_USERS_BY_MAC = "SELECT COUNT(*) FROM USER WHERE MAC_ADDRESS=?";
    private final String INSERT_USER_BY_MAC = "INSERT INTO USER (MAC_ADDRESS, HOSTNAME, IS_BLACKLISTED) values (?,?,0)";
    private final String DELETE_USER_BY_MAC = "ALTER TABLE USER DELETE WHERE MAC_ADDRESS=?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean isExist(String macAddress) {
        int cnt = Optional.ofNullable(jdbcTemplate.queryForObject(
                COUNT_USERS_BY_MAC,
                Integer.class,
                macAddress)).orElse(0);
        return cnt > 0;
    }

    @Override
    public void save(UserDto userDto) {
        jdbcTemplate.update(INSERT_USER_BY_MAC,
                userDto.getMacAddress(),
                userDto.getHostName());
    }

    @Override
    public void saveList(List<UserDto> userDtos) {
        if (userDtos.size() == 0) return;
        jdbcTemplate.batchUpdate(INSERT_USER_BY_MAC, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                UserDto userDto = userDtos.get(i);
                int idx = 0;
                ps.setString(++idx, userDto.getMacAddress());
                ps.setString(++idx, userDto.getHostName());
            }

            @Override
            public int getBatchSize() {
                return userDtos.size();
            }
        });
    }

    @Override
    public void remove(String macAddress) {
        jdbcTemplate.update(DELETE_USER_BY_MAC, macAddress);
    }

    @Override
    // TODO сомнительно, надо тестить
    public void removeList(List<String> macAddresses) {
        List<Object[]> args = new ArrayList<>();

        for (String macAddress : macAddresses) {
            Object[] arg = {macAddress};
            args.add(arg);
        }

        jdbcTemplate.batchUpdate(DELETE_USER_BY_MAC, args);
    }

    @Override
    public List<UserDto> findAll() {
        return jdbcTemplate.query(
                SELECT_ALL_USERS,
                (rs, rowNum) -> {
                    UserDto userDto = new UserDto();
                    userDto.setMacAddress(rs.getString(1));
                    userDto.setHostName(rs.getString(2));
                    return userDto;
                });
    }
}
*/
