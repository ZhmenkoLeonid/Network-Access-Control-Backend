package com.zhmenko.dao.jdbc;

import com.zhmenko.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {
    private static String SELECT_ALL_USERS = "SELECT * FROM USER";
    private static String SELECT_USER_BY_IP = "SELECT * FROM USER WHERE IP_ADDRESS=?";
    private static String INSERT_USER = "INSERT INTO USER (IP_ADDRESS) values (?)";
    private static String DELETE_USER = "ALTER TABLE USER DELETE WHERE IP_ADDRESS=?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public boolean isExist(String ipAddress) {
        return jdbcTemplate.queryForObject(
                SELECT_USER_BY_IP,
                (rs, rowNum) -> rs.getFetchSize() > 0,
                ipAddress);
    }

    @Override
    public void save(String ipAddress) {
        jdbcTemplate.update(INSERT_USER, ipAddress);
    }

    @Override
    public void saveList(List<String> ipAddresses) {
        if (ipAddresses.size() == 0) return;
        jdbcTemplate.batchUpdate(INSERT_USER, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, ipAddresses.get(i));
            }

            @Override
            public int getBatchSize() {
                return ipAddresses.size();
            }
        });
    }

    @Override
    public void remove(String ipAddress) {
        jdbcTemplate.update(DELETE_USER,ipAddress);
    }

    @Override
    // TODO сомнительно, надо тестить
    public void removeList(List<String> ipAddresses) {
        List<Object[]> args = new ArrayList<>();

        for (int i = 0; i < ipAddresses.size(); i++) {
            Object[] arg = {ipAddresses.get(i)};
            args.add(arg);
        }

        jdbcTemplate.batchUpdate(INSERT_USER, args);
    }

    @Override
    public List<String> findAll() {
        return jdbcTemplate.query(
                SELECT_ALL_USERS,
                (rs, rowNum) -> rs.getString(1));
    }
}
