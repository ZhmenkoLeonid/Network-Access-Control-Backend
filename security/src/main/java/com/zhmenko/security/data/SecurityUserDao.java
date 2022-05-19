package com.zhmenko.security.data;


import com.zhmenko.security.models.User;

import java.util.UUID;

public interface SecurityUserDao {
    User findByUUID(UUID uuid);

    User findByUsername(String username);

    void save(User user);
}
