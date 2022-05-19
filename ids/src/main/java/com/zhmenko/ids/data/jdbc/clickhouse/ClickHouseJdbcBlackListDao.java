package com.zhmenko.ids.data.jdbc.clickhouse;

import com.zhmenko.ids.data.BlackListDao;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ClickHouseJdbcBlackListDao implements BlackListDao {
    private final String GET_BLACKLIST = "SELECT IP_ADDRESS FROM NAC_USER where IS_BLACKLISTED=1";
    private final String INSERT_BLACKLIST_USER_BY_MAC = "ALTER TABLE NAC_USER UPDATE IS_BLACKLISTED=1 WHERE MAC_ADDRESS=?";
    private final String REMOVE_FROM_BLACKLIST_USER_BY_IP_ADDRESS = "ALTER TABLE NAC_USER UPDATE IS_BLACKLISTED=0 WHERE IP_ADDRESS=?";
    private final String IS_USER_BLOCKED_BY_IP_ADDRESS = "SELECT count(*) FROM NAC_USER WHERE IP_ADDRESS=?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<String> getBlackList() {
        return jdbcTemplate.query(GET_BLACKLIST,
                (rs,rowNum) -> rs.getString(1));
    }

    @Override
    public void insertUserIntoBlackListByMac(String macAddress) {
        jdbcTemplate.update(INSERT_BLACKLIST_USER_BY_MAC, macAddress);
    }

    @Override
    public void removeUserFromBlackListByMacAddress(String ipAddress) {
        jdbcTemplate.update(REMOVE_FROM_BLACKLIST_USER_BY_IP_ADDRESS, ipAddress);
    }

    @Override
    public boolean isBlocked(String macAddress) {
        int cnt = Optional.ofNullable(jdbcTemplate.queryForObject(
                IS_USER_BLOCKED_BY_IP_ADDRESS,
                Integer.class,
                macAddress)).orElse(0);
        return cnt > 0;
    }

}
