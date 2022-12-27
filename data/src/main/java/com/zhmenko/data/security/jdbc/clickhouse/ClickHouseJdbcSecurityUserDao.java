/*
package com.zhmenko.security.data.jdbc.clickhouse;

import com.zhmenko.security.models.SecurityUser;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ClickHouseJdbcSecurityUserDao implements SecurityUserDao {
    private final String SELECT_USER_BY_UUID = "SELECT * FROM SECURITY_USER WHERE ID=?";
    private final String SELECT_USER_BY_USERNAME = "SELECT * FROM SECURITY_USER WHERE USERNAME=?";
    private final String SAVE_USER = "INSERT INTO SECURITY_USER (ID, USERNAME, PASSWORD, ROLES) VALUES (?,?,?,?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public SecurityUser findByUUID(UUID uuid) {
        return jdbcTemplate.queryForObject(SELECT_USER_BY_UUID,
                (rs, rowNum) -> {
                    UUID id = UUID.fromString(rs.getString(1));
                    String username = rs.getString(2);
                    String password = rs.getString(3);
                    Array arr = rs.getArray(4);
                    Object arrObj = arr != null ? arr.getArray() : new String[]{};
                    String[] roles = (String[]) arr.getArray();

                    return new SecurityUser(id, username, password,
                            Arrays.stream(roles).map(Role::valueOf).collect(Collectors.toList()));
                },
                uuid);
    }

    @Override
    public SecurityUser findByUsername(String username) {
        try {
            SecurityUser securityUser = jdbcTemplate.queryForObject(SELECT_USER_BY_USERNAME,
                    (rs, rowNum) -> {
                        UUID id = UUID.fromString(rs.getString(1));
                        String password = rs.getString(3);
                        Array arr = rs.getArray(4);
                        String[] roles = (String[]) arr.getArray();

                        return new SecurityUser(id, username, password,
                                Arrays.stream(roles).map(Role::valueOf).collect(Collectors.toList()));
                    },
                    username);
            return securityUser;
        } catch (EmptyResultDataAccessException e){}
        return null;
    }

    @Override
    public void save(SecurityUser securityUser) {
        jdbcTemplate.update(SAVE_USER, securityUser.getId(), securityUser.getUsername(), securityUser.getPassword(), securityUser.getSecurityRoles());
    }
}
*/
