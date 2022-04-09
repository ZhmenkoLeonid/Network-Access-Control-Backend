package com.zhmenko.dao.jdbc;

import com.zhmenko.dao.BlackListDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JdbcBlackListDao implements BlackListDao {
    private static String GET_BLACKLIST = "SELECT * FROM USER_BLACKLIST";
    private static String INSERT_BLACKLIST_IP = "INSERT INTO USER_BLACKLIST (IP_ADDRESS) values (?)";
    private static String DELETE_BLACKLIST_IP = "ALTER TABLE USER_BLACKLIST DELETE WHERE IP_ADDRESS=?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<String> getBlackList() {
        return jdbcTemplate.query(GET_BLACKLIST,
                (rs,rowNum) -> rs.getString(1));
    }

    @Override
    public void insertIpIntoBlackList(String ipAddress) {
        jdbcTemplate.update(INSERT_BLACKLIST_IP, ipAddress);
    }

    @Override
    public void removeIpFromBlackList(String ipAddress) {
        jdbcTemplate.update(DELETE_BLACKLIST_IP, ipAddress);
    }

}
