package com.zhmenko.dao.jdbc;

import com.zhmenko.dao.UserOpenedPortsDao;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class JdbcUserOpenedPortsDao implements UserOpenedPortsDao {
    private static String INSERT_USER_PORT = "INSERT INTO USER_OPENED_PORT (USER_ID, PORT) values (?,?)";
    private static String DELETE_USER_PORT = "ALTER TABLE USER_OPENED_PORT DELETE WHERE USER_ID=? AND PORT=?";
    private static String DELETE_USER_PORTS = "ALTER TABLE USER_OPENED_PORT DELETE WHERE USER_ID=?";
    private static String SELECT_USER_PORTS = "SELECT PORT FROM USER_OPENED_PORT WHERE USER_ID=?";

    private JdbcTemplate jdbcTemplate;

    public JdbcUserOpenedPortsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(String userIp, int port) {
        jdbcTemplate.update(INSERT_USER_PORT, userIp, port);
    }

    @Override
    public void saveList(String userIp, List<Integer> ports) {
        if (ports.size() == 0) return;
        jdbcTemplate.batchUpdate(INSERT_USER_PORT, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, userIp);
                ps.setInt(2, ports.get(i));
            }

            @Override
            public int getBatchSize() {
                return ports.size();
            }
        });
    }

    @Override
    public void remove(String ipAddress, int port) {
        jdbcTemplate.update(DELETE_USER_PORT, ipAddress, port);
    }

    @Override
    public void removeAll(String ipAddress) {
        jdbcTemplate.update(DELETE_USER_PORTS, ipAddress);
    }

    @Override
    public List<Integer> findUserPorts(String ipAddress) {
        return jdbcTemplate.query(
                SELECT_USER_PORTS,
                (rs, rowNum) -> rs.getInt(2),
                ipAddress);
    }
}
