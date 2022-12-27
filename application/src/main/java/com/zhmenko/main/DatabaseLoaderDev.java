package com.zhmenko.main;

import com.zhmenko.data.nac.models.UserBlockInfoEntity;
import com.zhmenko.data.nac.models.NacRoleEntity;
import com.zhmenko.data.nac.models.UserDeviceEntity;
import com.zhmenko.data.nac.models.NetworkResourceEntity;
import com.zhmenko.data.nac.repository.NacRoleRepository;
import com.zhmenko.data.nac.repository.UserDeviceRepository;
import com.zhmenko.data.nac.repository.NetworkResourcesRepository;
import com.zhmenko.data.netflow.models.exception.UserNotExistException;
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
public class DatabaseLoaderDev implements CommandLineRunner {
    private final SecurityUserRepository securityUserRepository;
    private final SecurityRoleRepository securityRoleRepository;
    private final NetworkResourcesRepository networkResourcesRepository;

    private final NacRoleRepository nacRoleRepository;
    private final UserDeviceRepository userDeviceRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // NETWORK RESOURCES
        createNetworkResources();
        NetworkResourceEntity mailServerResource = networkResourcesRepository.findById(40).orElseThrow(RuntimeException::new);
        NetworkResourceEntity ftpServerResource = networkResourcesRepository.findById(90).orElseThrow(RuntimeException::new);
        NetworkResourceEntity exampleServiceResource = networkResourcesRepository.findById(1900).orElseThrow(RuntimeException::new);
        NetworkResourceEntity zeroPortServiceResource = networkResourcesRepository.findById(1).orElseThrow(RuntimeException::new);
        // NAC ROLES
        createNacRoles(mailServerResource, ftpServerResource, exampleServiceResource, zeroPortServiceResource);
        NacRoleEntity dataNacRole = nacRoleRepository.findByName("DATA_EMPLOYEE").orElseThrow(RuntimeException::new);
        NacRoleEntity employeeNacRole = nacRoleRepository.findByName("EXAMPLE_EMPLOYEE").orElseThrow(RuntimeException::new);
        // SECURITY USERS
        createSecurityRoles();
        SecurityRoleEntity adminSecurityRole = securityRoleRepository.findByName("ROLE_ADMIN").get();
        SecurityRoleEntity clientSecurityRole = securityRoleRepository.findByName("ROLE_CLIENT").get();

        createSecurityUsers(adminSecurityRole, clientSecurityRole, dataNacRole, employeeNacRole);
        SecurityUserEntity securityUser = securityUserRepository.findByUsername("client_vova").get();

        // NAC USERS
        createNacUsers(securityUser);
        UserDeviceEntity dataUser = userDeviceRepository.findByMacAddress("02:C1:98:7D:36:EE")
                .orElseThrow(UserNotExistException::new);
        UserDeviceEntity exampleUser = userDeviceRepository.findByMacAddress("27:D8:96:7F:56:DF")
                .orElseThrow(UserNotExistException::new);
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
        if (!networkResourcesRepository.existsById(1))
            networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(1).name("Zero Port Service").build());
    }

    public void createNacUsers(SecurityUserEntity securityUser) {
        if (userDeviceRepository.findById("02:C1:98:7D:36:EE").isEmpty()) {
            UserDeviceEntity sampleDataEmployee = UserDeviceEntity.builder()
                    .macAddress("02:C1:98:7D:36:EE")
                    .ipAddress("127.0.0.1")
                    .hostname("MacBook")
                    .blackListInfo(UserBlockInfoEntity.builder()
                            .isBlocked(false).build())
                    .securityUserEntity(securityUser)
                    .build();
            sampleDataEmployee.getBlackListInfo().setUserDeviceEntity(sampleDataEmployee);
            userDeviceRepository.save(sampleDataEmployee);
        }
        if (userDeviceRepository.findById("27:D8:96:7F:56:DF").isEmpty()) {
            UserDeviceEntity sampleExampleEmployee = UserDeviceEntity.builder()
                    .macAddress("27:D8:96:7F:56:DF")
                    .ipAddress("127.0.0.1")
                    .hostname("Personal computer")
                    .blackListInfo(UserBlockInfoEntity.builder()
                            .isBlocked(false).build())
                    .securityUserEntity(securityUser)
                    .build();
            sampleExampleEmployee.getBlackListInfo().setUserDeviceEntity(sampleExampleEmployee);
            userDeviceRepository.save(sampleExampleEmployee);
        }
    }

    public void createSecurityUsers(SecurityRoleEntity adminRole, SecurityRoleEntity clientRole,
                                    NacRoleEntity dataRole, NacRoleEntity employeeRole) {
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
        if (securityUserRepository.findByUsername("client_vova").isEmpty()) {
            Set<SecurityRoleEntity> roles = new HashSet<>();
            roles.add(clientRole);
            SecurityUserEntity securityUser = new SecurityUserEntity(UUID.randomUUID(),
                    "client_vova",
                    passwordEncoder.encode("vova_pass"),
                    roles,
                    Collections.emptySet(),
                    Collections.emptySet());
            securityUserRepository.save(securityUser);
        }
        if (securityUserRepository.findByUsername("sample example emp").isEmpty()) {
            Set<SecurityRoleEntity> securityRoles = new HashSet<>();
            securityRoles.add(clientRole);
            Set<NacRoleEntity> nacRoles = Set.of(dataRole, employeeRole);
            SecurityUserEntity securityUser = new SecurityUserEntity(UUID.randomUUID(),
                    "sample example emp",
                    passwordEncoder.encode("sample example emp"),
                    securityRoles,
                    nacRoles,
                    Collections.emptySet());
            securityUserRepository.save(securityUser);
        }
    }
}
