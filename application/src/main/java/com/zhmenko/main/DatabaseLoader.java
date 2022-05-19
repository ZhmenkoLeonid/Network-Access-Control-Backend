package com.zhmenko.main;

import com.zhmenko.ids.data.NacUserDao;
import com.zhmenko.security.data.SecurityUserDao;
import com.zhmenko.security.models.User;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class DatabaseLoader implements CommandLineRunner {
    private final SecurityUserDao securityUserDao;
    private final NacUserDao nacUserDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (securityUserDao.findByUsername("admin") == null) {
            User user = new User(UUID.randomUUID(), "admin", passwordEncoder.encode("changeit"));
            user.getRoles().add("ROLE_ADMIN");
            securityUserDao.save(user);
        }
        if (securityUserDao.findByUsername("client_vova") == null) {
            User user = new User(UUID.randomUUID(), "client_vova", passwordEncoder.encode("vova_pass"));
            user.getRoles().add("ROLE_CLIENT");
            securityUserDao.save(user);
        }
    }
}
