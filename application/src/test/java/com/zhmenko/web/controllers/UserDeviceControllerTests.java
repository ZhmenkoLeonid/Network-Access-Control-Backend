package com.zhmenko.web.controllers;

import com.zhmenko.data.nac.models.UserBlockInfoEntity;
import com.zhmenko.data.nac.models.NacRoleEntity;
import com.zhmenko.data.nac.models.UserDeviceEntity;
import com.zhmenko.data.nac.models.NetworkResourceEntity;
import com.zhmenko.data.nac.repository.NacRoleRepository;
import com.zhmenko.data.nac.repository.UserDeviceRepository;
import com.zhmenko.data.nac.repository.NetworkResourcesRepository;
import com.zhmenko.data.netflow.models.device.NetflowDevice;
import com.zhmenko.data.netflow.models.device.NetflowDeviceList;
import com.zhmenko.data.security.models.SecurityUserEntity;
import com.zhmenko.data.security.repository.SecurityUserRepository;
import com.zhmenko.web.nac.model.user_device.UserDeviceDto;
import com.zhmenko.web.nac.model.user_device.request.modify.UserDeviceModifyDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static com.zhmenko.web.controllers.util.Mac.randomMACAddress;
import static com.zhmenko.web.controllers.util.StringUtils.mapStringBody;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class UserDeviceControllerTests extends AbstractTest {
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

    @AfterEach
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

    // INSERT SECTOR - UNSUPPORTED FOR NOW

    // UPDATE SECTOR
    @Test
    void givenUserDevice_WhenUpdateUserDevice_ThenStatus200AndCorrectData() throws Exception {
        UserDeviceEntity nacUser = createUserDevice();
        //Set<NacRoleEntity> roles = nacUser.getRoles();
        //NacRoleEntity role = (NacRoleEntity) roles.toArray()[0];

        // create new role
/*        long cnt = nacRoleRepository.count();
        NacRoleEntity newRole = NacRoleEntity.builder()
                .name("new role")
                //.networkResources(role.getNetworkResources())
                .build();
        newRole = nacRoleRepository.save(newRole);
        assertEquals(cnt + 1, nacRoleRepository.count());*/

        UserDeviceModifyDto request = UserDeviceModifyDto.builder()
                .macAddress(nacUser.getMacAddress())
                .hostname("updated hostname")
                .isBlocked(false)
                //.roles(Set.of(newRole.getId().intValue()))
                .build();

        mockMvc.perform(
                        put(USER_DEVICE_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(
                        get(USER_DEVICE_URL + "/" + nacUser.getMacAddress()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UserDeviceDto actual = objectMapper.readValue(mapStringBody(mvcResult), UserDeviceDto.class);

        assertEquals(request.getMacAddress(), actual.getMacAddress());
        assertEquals(request.getHostname(), actual.getHostname());
        assertEquals(request.isBlocked(), actual.getBlackListInfo().isBlocked());

/*        List<Integer> actualRolesList = actual.getRoles().stream()
                .map(NacRoleDto::getId)
                .map(Long::intValue)
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());
        List<Integer> expectedList = new ArrayList<>(request.getRoles());
        expectedList.sort(Integer::compareTo);
        assertEquals(expectedList, actualRolesList);*/
    }
/*    @Test
    void givenNacUser_WhenUpdateNacUserWithNotExistingRoleId_ThenStatus400() throws Exception {
        UserDeviceEntity nacUser = createUserDevice();

        NacUserModifyDto request = NacUserModifyDto.builder()
                .macAddress(nacUser.getMacAddress())
                .hostname("updated hostname")
                .isBlocked(false)
                .roles(Set.of(10000000))
                .build();

        mockMvc.perform(
                        put(NAC_USER_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }*/
    @Test
    void givenUserDevice_WhenUpdateUserDeviceWithNotExistingMac_ThenStatus404() throws Exception {
        UserDeviceEntity nacUser = createUserDevice();

        // Generate another mac
        String newMac = randomMACAddress();
        while (newMac.equals(nacUser.getMacAddress())) newMac = randomMACAddress();

        UserDeviceModifyDto request = UserDeviceModifyDto.builder()
                .macAddress(newMac)
                .hostname("updated hostname")
                .isBlocked(nacUser.getBlackListInfo().getIsBlocked())
                //.roles(nacUser.getRoles().stream().map(e -> e.getId().intValue()).collect(Collectors.toSet()))
                .build();

        mockMvc.perform(
                        put(USER_DEVICE_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

/*    @Test
    void givenNacUser_WhenUpdateNotBlockedNacUserWithBlockedStatusTrue_ThenStatus200() throws Exception {
        UserDeviceEntity nacUser = createUserDevice();

        UserDeviceModifyDto request = UserDeviceModifyDto.builder()
                .macAddress(nacUser.getMacAddress())
                .hostname(nacUser.getHostname())
                .isBlocked(true)
*//*                .roles(Set.of(nacUser.getRoles().stream()
                        .findFirst()
                        .map(e -> e.getId().intValue())
                        .get()))*//*
                .build();
        log.info("request update user: " + request);
        mockMvc.perform(
                        put(USER_DEVICE_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(
                        get(USER_DEVICE_URL + "/" + nacUser.getMacAddress()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UserDeviceDto actual = objectMapper.readValue(mapStringBody(mvcResult), UserDeviceDto.class);

        assertEquals(request.getMacAddress(), actual.getMacAddress());
        assertEquals(request.getHostname(), actual.getHostname());
        // При блокировании роли должны отбрасываться
        //assertEquals(0, actual.getRoles().size());
        assertEquals(request.isBlocked(), actual.getBlackListInfo().isBlocked());
    }*/

    // GET SECTOR
    @Test
    void givenUserDevice_WhenGetByMacAddress_ThenStatus200AndCorrectData() throws Exception {
        // given
        UserDeviceEntity nacUser = createUserDevice();

        // when - then
        MvcResult mvcResult = mockMvc.perform(
                        get(USER_DEVICE_URL + "/" + nacUser.getMacAddress()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        UserDeviceDto actual = objectMapper.readValue(mapStringBody(mvcResult), UserDeviceDto.class);

        assertEquals(nacUser.getMacAddress(), actual.getMacAddress());
        assertEquals(nacUser.getHostname(), actual.getHostname());
        assertEquals(nacUser.getBlackListInfo().getIsBlocked(), actual.getBlackListInfo().isBlocked());

/*        List<Long> actualRolesList = actual.getRoles().stream()
                .map(NacRoleDto::getId)
                .sorted(Long::compareTo)
                .collect(Collectors.toList());
        List<Long> expectedList = nacUser.getRoles().stream()
                .map(NacRoleEntity::getId)
                .sorted(Long::compareTo)
                .collect(Collectors.toList());*/

        //assertEquals(expectedList, actualRolesList);
    }
    @Test
    void whenGetByMacAddressNotExistingDevice_ThenStatus404() throws Exception {
        mockMvc.perform(
                        get(USER_DEVICE_URL + "/" + randomMACAddress()))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    //Delete sector
    @Test
    void givenUser_WhenDeleteUser_ThenStatus200AndUserNotFound() throws Exception {
        UserDeviceEntity nacUser = createUserDevice();

        mockMvc.perform(
                get(USER_DEVICE_URL + "/" + nacUser.getMacAddress())
        ).andExpect(status().isOk());

        mockMvc.perform(
                delete(USER_DEVICE_URL + "/" + nacUser.getMacAddress())
        ).andExpect(status().isOk());

        mockMvc.perform(
                get(USER_DEVICE_URL + "/" + nacUser.getMacAddress())
        ).andExpect(status().isNotFound());
    }


    Set<NacRoleEntity> createNacRoles() {
        NetworkResourceEntity r1;
        NetworkResourceEntity r2;
        NetworkResourceEntity r3;

        networkResourcesRepository.save(r1 = NetworkResourceEntity.builder().resourcePort(1).name("r1").build());
        networkResourcesRepository.save(r2 = NetworkResourceEntity.builder().resourcePort(2).name("r2").build());
        networkResourcesRepository.save(r3 = NetworkResourceEntity.builder().resourcePort(3).name("r3").build());
        assertEquals(3, networkResourcesRepository.count());
        nacRoleRepository.save(NacRoleEntity.builder().name("role1").networkResources(Set.of(r1, r2)).build());
        nacRoleRepository.save(NacRoleEntity.builder().name("role2").networkResources(Set.of(r2, r3)).build());
        assertEquals(2, nacRoleRepository.count());
        return new HashSet<>(nacRoleRepository.findAll());
    }

    UserDeviceEntity createUserDevice(Set<NacRoleEntity> roles, SecurityUserEntity securityUserEntity) {
        String mac = randomMACAddress();
        long cntBfr = securityUserRepository.count();
        UserDeviceEntity entity = UserDeviceEntity.builder()
                .macAddress(mac)
                .blackListInfo(UserBlockInfoEntity.builder().isBlocked(false).macAddress(mac).build())
                .ipAddress("127.0.0.1")
                .hostname("1")
                .alerts(Collections.emptySet())
                .securityUserEntity(securityUserEntity)
                .build();
        entity.getBlackListInfo().setUserDeviceEntity(entity);
        netflowDeviceList.addDevice(entity);
        assertEquals(cntBfr + 1, userDeviceRepository.count());
        return entity;
    }

    UserDeviceEntity createUserDevice() {
        SecurityUserEntity securityUserEntity = createSecurityUser();
        String mac = randomMACAddress();
        long cntBfr = userDeviceRepository.count();
        UserDeviceEntity entity = UserDeviceEntity.builder()
                .macAddress(mac)
                .blackListInfo(UserBlockInfoEntity.builder().isBlocked(false).macAddress(mac).build())
                .ipAddress("127.0.0.1")
                .hostname("1")
                .alerts(Collections.emptySet())
                .securityUserEntity(securityUserEntity)
                .build();
        entity.getBlackListInfo().setUserDeviceEntity(entity);
        netflowDeviceList.addDevice(entity);
        assertEquals(cntBfr + 1, userDeviceRepository.count());
        return entity;
    }

    SecurityUserEntity createSecurityUser() {
        Set<NacRoleEntity> roles = createNacRoles();
        SecurityUserEntity securityUserEntity = SecurityUserEntity.builder()
                .id(UUID.randomUUID())
                .username("a")
                .password("a")
                .securityRoles(Collections.emptySet())
                .nacRoles(roles)
                .build();
        securityUserRepository.save(securityUserEntity);
        // 1 from admin default user
        assertEquals(2, securityUserRepository.count());
        return securityUserEntity;
    }


}
