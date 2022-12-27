package com.zhmenko.main;

import com.zhmenko.data.security.models.SecurityRoleEntity;
import com.zhmenko.data.security.models.SecurityUserEntity;
import com.zhmenko.data.security.repository.SecurityRoleRepository;
import com.zhmenko.data.security.repository.SecurityUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Profile("prod")
public class DatabaseLoaderProd implements CommandLineRunner {
    private final static String ADMIN_ROLE = "ROLE_ADMIN";
    private final static String CLIENT_ROLE = "ROLE_CLIENT";
    private final SecurityRoleRepository securityRoleRepository;
    private final SecurityUserRepository securityUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createSecurityRolesIfNotExist();
        SecurityRoleEntity adminSecurityRole = securityRoleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(IllegalStateException::new);
        createAdminUserIfNotExist(adminSecurityRole);
        if (securityUserRepository.findByUsername("admin").isEmpty()) throw new IllegalStateException();
    }

    private void createAdminUserIfNotExist(SecurityRoleEntity adminRole) {
        if (securityUserRepository.findByUsername("admin").isEmpty()) {
            Set<SecurityRoleEntity> roles = new HashSet<>();
            roles.add(adminRole);
            SecurityUserEntity securityUser = new SecurityUserEntity(UUID.randomUUID(),
                    "admin",
                    passwordEncoder.encode("changeit"),
                    roles,
                    Collections.emptySet(),
                    Collections.emptySet());
            securityUserRepository.save(securityUser);
        }
    }

    public void createSecurityRolesIfNotExist() {
        if (securityRoleRepository.findByName(ADMIN_ROLE).isEmpty())
            securityRoleRepository.save(SecurityRoleEntity.builder().name(ADMIN_ROLE).build());
        if (securityRoleRepository.findByName(CLIENT_ROLE).isEmpty())
            securityRoleRepository.save(SecurityRoleEntity.builder().name(CLIENT_ROLE).build());
    }
}
