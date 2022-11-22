package com.zhmenko.main;

import com.zhmenko.data.nac.models.BlackListEntity;
import com.zhmenko.data.nac.models.NacRoleEntity;
import com.zhmenko.data.nac.models.NacUserEntity;
import com.zhmenko.data.nac.models.NetworkResourceEntity;
import com.zhmenko.data.nac.repository.NacRoleRepository;
import com.zhmenko.data.nac.repository.NacUserRepository;
import com.zhmenko.data.nac.repository.NetworkResourcesRepository;
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
@Profile("dev")
public class DatabaseLoader implements CommandLineRunner {
    private final SecurityUserRepository securityUserRepository;
    private final SecurityRoleRepository securityRoleRepository;
    private final NetworkResourcesRepository networkResourcesRepository;

    private final NacRoleRepository nacRoleRepository;
    private final NacUserRepository nacUserRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createSecurityRoles();
        SecurityRoleEntity adminRole = securityRoleRepository.findByName("ROLE_ADMIN").get();
        SecurityRoleEntity clientRole = securityRoleRepository.findByName("ROLE_CLIENT").get();

        createSecurityUsers(adminRole, clientRole);
        SecurityUserEntity securityUser = securityUserRepository.findByUsername("client_vova").get();

        createNetworkResources();
        NetworkResourceEntity mailServerResource = networkResourcesRepository.findById(40).orElseThrow(RuntimeException::new);
        NetworkResourceEntity ftpServerResource = networkResourcesRepository.findById(90).orElseThrow(RuntimeException::new);
        NetworkResourceEntity exampleServiceResource = networkResourcesRepository.findById(1900).orElseThrow(RuntimeException::new);
        NetworkResourceEntity zeroPortServiceResource = networkResourcesRepository.findById(0).orElseThrow(RuntimeException::new);

        createNacRoles(mailServerResource, ftpServerResource, exampleServiceResource, zeroPortServiceResource);
        NacRoleEntity dataRole = nacRoleRepository.findByName("DATA_EMPLOYEE").orElseThrow(RuntimeException::new);
        NacRoleEntity employeeRole = nacRoleRepository.findByName("EXAMPLE_EMPLOYEE").orElseThrow(RuntimeException::new);

        createNacUsers(securityUser, dataRole, employeeRole);
        NacUserEntity dataUser = nacUserRepository.findByMacAddress("02:C1:98:7D:36:EE");
        NacUserEntity exampleUser = nacUserRepository.findByMacAddress("27:D8:96:7F:56:DF");
        System.out.println(dataUser);
        System.out.println(exampleUser);
        System.out.println(securityUserRepository.findByUsername("client_vova").get());
    }

    public void createSecurityRoles() {
        if (securityRoleRepository.findByName("ROLE_ADMIN").isEmpty())
            securityRoleRepository.save(SecurityRoleEntity.builder().name("ROLE_ADMIN").build());
        if (securityRoleRepository.findByName("ROLE_CLIENT").isEmpty())
            securityRoleRepository.save(SecurityRoleEntity.builder().name("ROLE_CLIENT").build());
    }

    public void createNacRoles(NetworkResourceEntity mailServerResource,
                               NetworkResourceEntity ftpServerResource,
                               NetworkResourceEntity exampleServiceResource,
                               NetworkResourceEntity zeroPortServiceResource) {
        if (!nacRoleRepository.existsByName("DATA_EMPLOYEE")) {
            Set<NetworkResourceEntity> dataEmployeeResources = new HashSet<>();
            dataEmployeeResources.add(mailServerResource);
            dataEmployeeResources.add(ftpServerResource);
            nacRoleRepository.save(NacRoleEntity.builder()
                    .name("DATA_EMPLOYEE")
                    .networkResources(dataEmployeeResources)
                    .build());
        }
        if (!nacRoleRepository.existsByName("EXAMPLE_EMPLOYEE")) {
            Set<NetworkResourceEntity> exampleEmployeeResources = new HashSet<>();
            exampleEmployeeResources.add(mailServerResource);
            exampleEmployeeResources.add(exampleServiceResource);
            exampleEmployeeResources.add(zeroPortServiceResource);
            nacRoleRepository.save(NacRoleEntity.builder()
                    .name("EXAMPLE_EMPLOYEE")
                    .networkResources(exampleEmployeeResources)
                    .build());
        }
    }

    public void createNetworkResources() {
        if (!networkResourcesRepository.existsById(40))
            networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(40).name("Mail Server").build());
        if (!networkResourcesRepository.existsById(90))
            networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(90).name("FTP Server").build());
        if (!networkResourcesRepository.existsById(1900))
            networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(1900).name("Example Service").build());
        if (!networkResourcesRepository.existsById(0))
            networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(0).name("Zero Port Service").build());
    }

    public void createNacUsers(SecurityUserEntity securityUser,
                               NacRoleEntity dataRole,
                               NacRoleEntity employeeRole) {
        if (nacUserRepository.findById("02:C1:98:7D:36:EE").isEmpty()) {
            Set<NacRoleEntity> dataRoles = new HashSet<>();
            dataRoles.add(dataRole);
            NacUserEntity sampleDataEmployee = NacUserEntity.builder()
                    .macAddress("02:C1:98:7D:36:EE")
                    .ipAddress("127.0.0.1")
                    .hostname("sample data employee")
                    .userNacRoleEntities(dataRoles)
                    .blackListInfo(BlackListEntity.builder()
                            .isBlocked(false).build())
                    .securityUserEntity(securityUser)
                    .build();
            sampleDataEmployee.getBlackListInfo().setNacUserEntity(sampleDataEmployee);
            nacUserRepository.save(sampleDataEmployee);
        }
        if (nacUserRepository.findById("27:D8:96:7F:56:DF").isEmpty()) {
            Set<NacRoleEntity> exampleRoles = new HashSet<>();
            exampleRoles.add(employeeRole);
            NacUserEntity sampleExampleEmployee = NacUserEntity.builder()
                    .macAddress("27:D8:96:7F:56:DF")
                    .ipAddress("127.0.0.1")
                    .hostname("sample example employee")
                    .userNacRoleEntities(exampleRoles)
                    .blackListInfo(BlackListEntity.builder()
                            .isBlocked(false).build())
                    .securityUserEntity(securityUser)
                    .build();
            sampleExampleEmployee.getBlackListInfo().setNacUserEntity(sampleExampleEmployee);
            nacUserRepository.save(sampleExampleEmployee);
        }
    }

    public void createSecurityUsers(SecurityRoleEntity adminRole, SecurityRoleEntity clientRole) {
        if (securityUserRepository.findByUsername("admin").isEmpty()) {
            Set<SecurityRoleEntity> roles = new HashSet<>();
            roles.add(adminRole);
            SecurityUserEntity securityUser = new SecurityUserEntity(UUID.randomUUID(),
                    "admin",
                    passwordEncoder.encode("changeit"),
                    roles,
                    Collections.emptySet());
            securityUserRepository.save(securityUser);
        }
        if (securityUserRepository.findByUsername("client_vova").isEmpty()) {
            Set<SecurityRoleEntity> roles = new HashSet<>();
            roles.add(clientRole);
            SecurityUserEntity securityUser = new SecurityUserEntity(UUID.randomUUID(),
                    "client_vova",
                    passwordEncoder.encode("vova_pass"),
                    roles,
                    Collections.emptySet());
            securityUserRepository.save(securityUser);
        }
        if (securityUserRepository.findByUsername("test_client").isEmpty()) {
            Set<SecurityRoleEntity> roles = new HashSet<>();
            roles.add(clientRole);
            SecurityUserEntity securityUser = new SecurityUserEntity(UUID.randomUUID(),
                    "test_client",
                    passwordEncoder.encode("test_client"),
                    roles,
                    Collections.emptySet());
            securityUserRepository.save(securityUser);
        }
    }
}
