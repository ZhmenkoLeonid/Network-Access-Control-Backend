package com.zhmenko.web.controllers;

import com.zhmenko.data.nac.entity.NacRoleEntity;
import com.zhmenko.data.nac.entity.NetworkResourceEntity;
import com.zhmenko.data.nac.entity.UserBlockInfoEntity;
import com.zhmenko.data.nac.entity.UserDeviceEntity;
import com.zhmenko.data.nac.repository.NacRoleRepository;
import com.zhmenko.data.nac.repository.NetworkResourcesRepository;
import com.zhmenko.data.nac.repository.UserDeviceRepository;
import com.zhmenko.data.netflow.models.device.NetflowDevice;
import com.zhmenko.data.netflow.models.device.NetflowDeviceList;
import com.zhmenko.data.security.models.SecurityRoleEntity;
import com.zhmenko.data.security.models.SecurityUserEntity;
import com.zhmenko.data.security.repository.SecurityRoleRepository;
import com.zhmenko.data.security.repository.SecurityUserRepository;
import com.zhmenko.web.controllers.util.StringUtils;
import com.zhmenko.web.nac.model.nacrole.response.NacRoleDto;
import com.zhmenko.web.security.model.authcontroller.request.SignupRequest;
import com.zhmenko.web.security.model.securityusercontroller.SecurityRoleDto;
import com.zhmenko.web.security.model.securityusercontroller.SecurityUserDto;
import com.zhmenko.web.security.model.securityusercontroller.request.modify.SecurityUserModifyDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;
import java.util.stream.Collectors;

import static com.zhmenko.web.controllers.util.Mac.randomMACAddress;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class SecurityUserControllerTest extends AbstractTest {
    private final String SECURITY_USER_URL = baseApiUrl + "/security-user";
    private final String AUTH_URL = baseApiUrl + "/api/auth";

    private final String USER_DEVICE_URL = baseApiUrl + "/user-device";
    @Autowired
    private UserDeviceRepository userDeviceRepository;
    @Autowired
    private NetflowDeviceList netflowDeviceList;
    @Autowired
    private NacRoleRepository nacRoleRepository;
    @Autowired
    private NetworkResourcesRepository networkResourcesRepository;
    @Autowired
    private SecurityUserRepository securityUserRepository;
    @Autowired
    private SecurityRoleRepository securityRoleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void deleteUserDeviceRepositoryRecords() {
        log.info("start clear netflow users");
        for (NetflowDevice netflowDevice : netflowDeviceList.getUserList()) {
            log.info("clear: " + netflowDevice);
            netflowDeviceList.deleteDevice(netflowDevice.getMacAddress());
        }
        log.info("end clear netflow users");
        assertEquals(0, netflowDeviceList.getUserList().size());
        assertEquals(0, userDeviceRepository.count());
    }

    // INSERT
    @Test
    void givenSecurityUser_WhenPostUser_ThenStatus201AndShouldBeCorrectData() throws Exception {
        Set<String> securityRoles = createSecurityRoles().stream()
                .map(SecurityRoleEntity::getName)
                .collect(Collectors.toSet());
        SignupRequest signupRequest = SignupRequest.builder()
                .username("test")
                .password("password")
                .roles(securityRoles)
                .build();
        mockMvc.perform(post(AUTH_URL + "/register")
                        .content(objectMapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Optional<SecurityUserEntity> byUsername = securityUserRepository.findByUsername(signupRequest.getUsername());
        assertTrue(byUsername.isPresent());
        SecurityUserEntity actual = byUsername.get();

        assertTrue(passwordEncoder.matches(signupRequest.getPassword(), actual.getPassword()));
        assertEquals(signupRequest.getRoles().stream().sorted().collect(Collectors.toList()),
                actual.getSecurityRoles().stream().map(SecurityRoleEntity::getName).sorted().collect(Collectors.toList()));
    }

    // GET
    @Test
    void givenUser_WhenGetUser_ThenStatus200AndCorrectData() throws Exception {
        //given
        SecurityUserEntity securityUser = createSecurityUser();
        // create 2 devices for user
        List<UserDeviceEntity> randomUserDevices = createRandomUserDevices(securityUser, 2);

        MvcResult mvcResult = mockMvc.perform(get(SECURITY_USER_URL + "/" + securityUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        SecurityUserDto actual = objectMapper.readValue(StringUtils.mapStringBody(mvcResult), SecurityUserDto.class);

        assertEquals(securityUser.getUsername(), actual.getUsername());

        assertEquals(
                securityUser.getNacRoles().stream()
                        .map(NacRoleEntity::getId)
                        .sorted()
                        .collect(Collectors.toList())
                ,
                actual.getNacRoles().stream()
                        .map(NacRoleDto::getId)
                        .sorted()
                        .collect(Collectors.toList())
        );

        assertEquals(
                securityUser.getSecurityRoles().stream()
                        .map(SecurityRoleEntity::getId)
                        .sorted()
                        .collect(Collectors.toList())
                ,
                actual.getSecurityRoles().stream()
                        .map(SecurityRoleDto::getId)
                        .sorted()
                        .collect(Collectors.toList())
        );
    }

    // UPDATE
    @Test
    void givenUser_WhenUpdateUser_ThenResult200AndShouldReturnUpdatedUser() throws Exception {
        //given
        SecurityUserEntity securityUser = createSecurityUser();
        // create 2 devices for user
        List<UserDeviceEntity> randomUserDevices = createRandomUserDevices(securityUser, 2);

        // create new role
        NacRoleEntity newRole = createNacRole("new role", Collections.emptySet());

        SecurityUserModifyDto updatedUser = SecurityUserModifyDto.builder()
                .id(securityUser.getId())
                .username("updated username")
                .nacRoles(Set.of(newRole.getId()))
                .securityRoles(Collections.emptySet())
                .build();

        // when
        mockMvc.perform(put(SECURITY_USER_URL)
                .content(objectMapper.writeValueAsString(updatedUser))
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk());

        Optional<SecurityUserEntity> securityUserEntityOpt = securityUserRepository.findById(securityUser.getId());
        assertTrue(securityUserEntityOpt.isPresent());
        SecurityUserEntity actual= securityUserEntityOpt.get();

        assertEquals(updatedUser.getUsername(), actual.getUsername());
        assertEquals(
                updatedUser.getNacRoles().stream()
                        .sorted()
                        .collect(Collectors.toList())
                ,
                actual.getNacRoles().stream()
                        .map(NacRoleEntity::getId)
                        .sorted()
                        .collect(Collectors.toList())
        );
        assertEquals(updatedUser.getSecurityRoles().size(), actual.getSecurityRoles().size());
/*        assertEquals(randomUserDevices.stream().map(UserDeviceEntity::getMacAddress).sorted().collect(Collectors.toList()),
                actual.getNacUsers().stream().map(UserDeviceDto::getMacAddress).sorted().collect(Collectors.toList()));*/
    }
    // DELETE
    @Test
    void givenUser_WhenDeleteUserAndTryGetUser_ThenStatus404() throws Exception {
        //given
        SecurityUserEntity securityUser = createSecurityUser();
        // create 2 devices for user
        List<UserDeviceEntity> randomUserDevices = createRandomUserDevices(securityUser, 2);

        //when-then
        // check that exist
        assertTrue(securityUserRepository.existsById(securityUser.getId()));
        // delete
        mockMvc.perform(delete(SECURITY_USER_URL + "/" + securityUser.getId()))
                .andExpect(status().isOk());
        // check that user not exist
        assertFalse(securityUserRepository.existsById(securityUser.getId()));

        // check that user devices deleted too
        for (UserDeviceEntity userDeviceEntity : randomUserDevices) {
            assertFalse(userDeviceRepository.existsById(userDeviceEntity.getMacAddress()));
        }
    }

    SecurityUserEntity createSecurityUser() {
        Set<NacRoleEntity> roles = createNacRoles();

        long cnt = securityUserRepository.count();
        SecurityUserEntity securityUserEntity = SecurityUserEntity.builder()
                .id(UUID.randomUUID())
                .username("test")
                .password("pass")
                .securityRoles(Collections.emptySet())
                .nacRoles(roles)
                .build();
        securityUserEntity = securityUserRepository.save(securityUserEntity);
        assertEquals(cnt + 1, securityUserRepository.count());
        return securityUserEntity;
    }

    List<UserDeviceEntity> createRandomUserDevices(SecurityUserEntity securityUserEntity, long cnt) {
        List<UserDeviceEntity> res = new ArrayList<>();
        for (long i = 0; i < cnt; i++) {
            res.add(createUserDevice(securityUserEntity));
        }
        return res;
    }

    UserDeviceEntity createUserDevice(SecurityUserEntity securityUserEntity) {
        String mac = randomMACAddress();
        long cntBfr = userDeviceRepository.count();
        UserDeviceEntity entity = UserDeviceEntity.builder()
                .macAddress(mac)
                .blackListInfo(UserBlockInfoEntity.builder().isBlocked(false).macAddress(mac).build())
                .ipAddress("127.0.0.1")
                .hostname(mac + " name")
                .alerts(Collections.emptySet())
                .securityUserEntity(securityUserEntity)
                .build();
        entity.getBlackListInfo().setUserDeviceEntity(entity);
        netflowDeviceList.addDevice(entity);
        assertEquals(cntBfr + 1, userDeviceRepository.count());
        return entity;
    }

    Set<NacRoleEntity> createNacRoles() {
        NetworkResourceEntity r1;
        NetworkResourceEntity r2;
        NetworkResourceEntity r3;

        long cntBfrInsertResource = networkResourcesRepository.count();
        networkResourcesRepository.save(r1 = NetworkResourceEntity.builder().resourcePort(1).name("r1").build());
        networkResourcesRepository.save(r2 = NetworkResourceEntity.builder().resourcePort(2).name("r2").build());
        networkResourcesRepository.save(r3 = NetworkResourceEntity.builder().resourcePort(3).name("r3").build());
        assertEquals(cntBfrInsertResource + 3, networkResourcesRepository.count());

        NacRoleEntity role1;
        NacRoleEntity role2;
        long cntBfrInsertRole = nacRoleRepository.count();
        nacRoleRepository.save(role1 = NacRoleEntity.builder().name("role1").networkResources(Set.of(r1, r2)).build());
        nacRoleRepository.save(role2 = NacRoleEntity.builder().name("role2").networkResources(Set.of(r2, r3)).build());
        assertEquals(cntBfrInsertRole + 2, nacRoleRepository.count());
        return new HashSet<>(Set.of(role1, role2));
    }

    NacRoleEntity createNacRole(String name, Set<NetworkResourceEntity> networkResourceEntities) {
        return nacRoleRepository.save(NacRoleEntity.builder().name(name).networkResources(networkResourceEntities).build());
    }

    Set<SecurityRoleEntity> createSecurityRoles() {
        long cnt = securityRoleRepository.count();
        Optional<SecurityRoleEntity> roleAdminOpt = securityRoleRepository.findByName("ROLE_ADMIN");
        SecurityRoleEntity roleAdmin;
        if (roleAdminOpt.isEmpty()) {
            roleAdmin = SecurityRoleEntity.builder().name("ROLE_ADMIN").build();
            securityRoleRepository.save(roleAdmin);
            cnt++;
        } else roleAdmin = roleAdminOpt.get();

        Optional<SecurityRoleEntity> roleClientOpt = securityRoleRepository.findByName("ROLE_CLIENT");
        SecurityRoleEntity roleClient;
        if (roleClientOpt.isEmpty()) {
            roleClient = SecurityRoleEntity.builder().name("ROLE_CLIENT").build();
            securityRoleRepository.save(roleClient);
            cnt++;
        } else roleClient = roleClientOpt.get();

        assertEquals(cnt, securityRoleRepository.count());
        return new HashSet<>(Set.of(roleAdmin, roleClient));
    }
}
