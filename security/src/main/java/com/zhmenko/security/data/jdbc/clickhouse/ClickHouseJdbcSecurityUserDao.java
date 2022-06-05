package com.zhmenko.security.data.jdbc.clickhouse;

import com.zhmenko.security.data.SecurityUserDao;
import com.zhmenko.security.models.User;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class ClickHouseJdbcSecurityUserDao implements SecurityUserDao {
    private final String SELECT_USER_BY_UUID = "SELECT * FROM SECURITY_USER WHERE ID=?";
    private final String SELECT_USER_BY_USERNAME = "SELECT * FROM SECURITY_USER WHERE USERNAME=?";
    private final String SAVE_USER = "INSERT INTO SECURITY_USER (ID, USERNAME, PASSWORD, ROLES) VALUES (?,?,?,?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User findByUUID(UUID uuid) {
        return jdbcTemplate.queryForObject(SELECT_USER_BY_UUID,
                (rs, rowNum) -> {
                    UUID id = UUID.fromString(rs.getString(1));
                    String username = rs.getString(2);
                    String password = rs.getString(3);
                    Array arr = rs.getArray(4);
                    Object arrObj = arr != null ? arr.getArray() : new String[]{};
                    String[] roles = (String[]) arr.getArray();

                    return new User(id, username, password, new HashSet<>(Arrays.asList(roles)));
                },
                uuid);
    }

    @Override
    public User findByUsername(String username) {
        try {
            User user = jdbcTemplate.queryForObject(SELECT_USER_BY_USERNAME,
                    (rs, rowNum) -> {
                        UUID id = UUID.fromString(rs.getString(1));
                        String password = rs.getString(3);
                        Array arr = rs.getArray(4);
                        String[] roles = (String[]) arr.getArray();

                        return new User(id, username, password, new HashSet<>(Arrays.asList(roles)));
                    },
                    username);
            return user;
        } catch (EmptyResultDataAccessException e){}
        return null;
    }

    @Override
    public void save(User user) {
        jdbcTemplate.update(SAVE_USER, user.getId(), user.getUsername(),user.getPassword(),user.getRoles());
    }
}
