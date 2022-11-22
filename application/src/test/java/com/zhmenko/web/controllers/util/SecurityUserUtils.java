package com.zhmenko.web.controllers.util;

import com.zhmenko.data.security.models.SecurityRoleEntity;
import com.zhmenko.data.security.models.SecurityUserEntity;
import com.zhmenko.data.security.repository.SecurityRoleRepository;
import com.zhmenko.data.security.repository.SecurityUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SecurityUserUtils {
    @Autowired
    private SecurityRoleRepository securityRoleRepository;
    @Autowired
    private SecurityUserRepository securityUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void addAdminUser(String username) {
        Optional<SecurityRoleEntity> adminRoleOpt = securityRoleRepository.findByName("ROLE_ADMIN");
        SecurityRoleEntity adminRole;
        if (adminRoleOpt.isEmpty())
           adminRole = securityRoleRepository.save(SecurityRoleEntity.builder().name("ROLE_ADMIN").build());
        else adminRole = adminRoleOpt.get();

/*        if (securityRoleRepository.findByName("CLIENT").isEmpty())
            securityRoleRepository.save(SecurityRoleEntity.builder().name("CLIENT").build());*/

        if (securityUserRepository.findByUsername("admin").isEmpty()) {
            Set<SecurityRoleEntity> roles = new HashSet<>();
            roles.add(adminRole);
            SecurityUserEntity securityUser = new SecurityUserEntity(UUID.randomUUID(),
                    username,
                    passwordEncoder.encode("changeit"),
                    roles,
                    Collections.emptySet());
            securityUserRepository.save(securityUser);
        }
    }

}
