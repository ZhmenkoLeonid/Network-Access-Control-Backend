package com.zhmenko.web.controllers;


import com.zhmenko.data.nac.models.NetworkResourceEntity;
import com.zhmenko.data.nac.repository.NetworkResourcesRepository;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import com.zhmenko.web.nac.model.nacrole.request.insert.NacRoleInsertDto;
import com.zhmenko.web.nac.model.nacrole.request.insert.NacRolesInsertRequest;
import com.zhmenko.web.nac.model.nacrole.request.modify.NacRoleModifyDto;
import com.zhmenko.web.nac.model.nacrole.request.modify.NacRolesModifyRequest;
import com.zhmenko.web.nac.model.nacrole.response.NacRoleDto;
import com.zhmenko.web.nac.model.nacrole.response.NacRolesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.zhmenko.web.controllers.util.StringUtils.mapStringBody;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NacRoleControllerTests extends AbstractTest {
    @Autowired
    private NetworkResourcesRepository networkResourcesRepository;

    private final String NAC_ROLE_CONTROLLER_URL = baseApiUrl + "/nac-role";

    // ADD_ROLES SECTOR
    @Test
    void givenNacRoles_WhenPostRoles_ThenStatus201AndCorrectData() throws Exception {
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(11).name("r1").build());
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(1000).name("r2").build());
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(10000).name("r3").build());

        List<Integer> networkResourceDtos1 = new ArrayList<>();
        networkResourceDtos1.add(11);
        networkResourceDtos1.add(1000);

        NacRoleInsertDto nacRoleInsertDto1 = NacRoleInsertDto.builder()
                .name("role1")
                .networkResources(networkResourceDtos1)
                .build();

        List<Integer> networkResourceDtos2 = new ArrayList<>();
        networkResourceDtos2.add(10000);

        NacRoleInsertDto nacRoleInsertDto2 = NacRoleInsertDto.builder()
                .name("role2")
                .networkResources(networkResourceDtos2)
                .build();
        List<NacRoleInsertDto> roles = List.of(nacRoleInsertDto1, nacRoleInsertDto2);

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder()
                .roles(roles).build();
        // when - then
        List<NacRoleDto> actual = insertRolesAndGetSorted(nacRolesInsertRequest);

        // сортируем для правильности проверки, т.к. ручка может вернуть ресурсы в любом порядке
        sortNacRoleObjects(actual);

        roleInsertCompareList(roles, actual);
    }
    @Test
    void givenNacRoleWithoutNetworkResources_WhenPostRole_ThenStatus201AndCorrectData() throws Exception {
        NacRoleInsertDto nacRoleInsertDto = NacRoleInsertDto.builder()
                .name("role1")
                .build();

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder().roles(List.of(nacRoleInsertDto)).build();
        // when
        mockMvc.perform(
                        post(NAC_ROLE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(nacRolesInsertRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(result ->
                        Assertions.assertEquals("Вставка ролей прошла успешно!", mapStringBody(result)))
                .andReturn();

        MvcResult mvcResult = mockMvc.perform(
                        get(baseApiUrl + "/nac-role/role1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        // then
        NacRoleModifyDto actual = objectMapper.readValue(mapStringBody(mvcResult), NacRoleModifyDto.class);

        assertEquals(nacRoleInsertDto.getName(), actual.getName());
        assertEquals(Collections.emptyList(), actual.getNetworkResources());
    }

    @Test
    void givenNacRole_WhenPostRoleWithNullOrBlankName_ThenStatus400() throws Exception {
        NacRoleInsertDto nacRoleInsertDto = NacRoleInsertDto.builder()
                .networkResources(List.of(11))
                .build();

        NacRolesInsertRequest request = NacRolesInsertRequest.builder()
                .roles(List.of(nacRoleInsertDto))
                .build();
        // when null
        mockMvc.perform(
                        post(NAC_ROLE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());

        // when blank
        request.getRoles().get(0).setName("");
        mockMvc.perform(
                        post(NAC_ROLE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }
    @Test
    void givenNacRoleWithNotExistingNetworkResource_WhenPostRole_ThenStatus400() throws Exception {
        NacRoleInsertDto nacRoleInsertDto = NacRoleInsertDto.builder()
                .name("role1")
                .networkResources(List.of(11))
                .build();

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder().roles(List.of(nacRoleInsertDto)).build();
        // when
        mockMvc.perform(
                        post(NAC_ROLE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(nacRolesInsertRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }
    @Test
    void givenNacRolesWithIdenticalNames_WhenPostSecondRole_ThenStatus400() throws Exception {
        NacRoleInsertDto nacRoleInsertDto = NacRoleInsertDto.builder()
                .name("role1")
                .build();

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder().roles(List.of(nacRoleInsertDto)).build();
        // when-then
        mockMvc.perform(
                        post(NAC_ROLE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(nacRolesInsertRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(
                        post(NAC_ROLE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(nacRolesInsertRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void givenNacRolesWithIdenticalNames_WhenPostRoles_ThenStatus400() throws Exception {
        NacRoleInsertDto nacRoleInsertDto = NacRoleInsertDto.builder()
                .name("role1")
                .build();

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder()
                .roles(List.of(nacRoleInsertDto, nacRoleInsertDto)).build();
        // when-then
        mockMvc.perform(
                        post(NAC_ROLE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(nacRolesInsertRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void givenNacRoleHavingDuplicatingNetworkResources_WhenPostRole_ThenStatus400() throws Exception {
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(11).name("r1").build());
        NacRoleInsertDto nacRoleInsertDto = NacRoleInsertDto.builder()
                .name("role1")
                .networkResources(List.of(11, 11))
                .build();

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder().roles(List.of(nacRoleInsertDto)).build();
        // when-then
        mockMvc.perform(
                        post(NAC_ROLE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(nacRolesInsertRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    // UPDATE_ROLES SECTOR
    @Test
    void givenNacRoles_WhenUpdateRoles_ThenStatus201AndCorrectData() throws Exception {
        // given
        //insert
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(11).name("r1").build());
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(1000).name("r2").build());
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(10000).name("r3").build());

        NacRoleInsertDto nacRoleInsertDto1 = NacRoleInsertDto.builder()
                .name("role1")
                .networkResources(List.of(11, 1000))
                .build();

        NacRoleInsertDto nacRoleInsertDto2 = NacRoleInsertDto.builder()
                .name("role2")
                .networkResources(List.of(10000))
                .build();
        List<NacRoleInsertDto> insertRoles = List.of(nacRoleInsertDto1, nacRoleInsertDto2);

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder()
                .roles(insertRoles).build();

        List<NacRoleDto> vals = insertRolesAndGetSorted(nacRolesInsertRequest);

        // update data
        NacRoleModifyDto uNacRoleInsertDto1 = NacRoleModifyDto.builder()
                .id(vals.get(0).getId())
                .name("updated role 1")
                .networkResources(List.of(11, 10000))
                .build();
        NacRoleModifyDto uNacRoleInsertDto2 = NacRoleModifyDto.builder()
                .id(vals.get(1).getId())
                .name("updated role 2")
                .networkResources(List.of(1000, 10000))
                .build();

        NacRolesModifyRequest updateRequest = NacRolesModifyRequest.builder()
                .roles(List.of(uNacRoleInsertDto1, uNacRoleInsertDto2))
                .build();
        // when
        mockMvc.perform(
                        put(NAC_ROLE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(updateRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result ->
                        Assertions.assertEquals("Обновление ролей прошло успешно!", mapStringBody(result)))
                .andReturn();

        MvcResult mvcResultAfterUpdate = mockMvc.perform(
                        get(NAC_ROLE_CONTROLLER_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        //then
        List<NacRoleDto> actual =
                objectMapper.readValue(mapStringBody(mvcResultAfterUpdate), NacRolesResponse.class).getRoles();

        // сортируем для правильности проверки, т.к. ручка может вернуть ресурсы в любом порядке
        sortNacRoleObjects(actual);

        roleModifyCompareList(updateRequest.getRoles(), actual);
    }
    @Test
    void givenNacRoleWithNotExistingId_WhenUpdateRole_ThenStatus400() throws Exception {
        // given
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(11).name("r1").build());

        NacRoleModifyDto uNacRoleInsertDto1 = NacRoleModifyDto.builder()
                .id(1)
                .name("updated role 1")
                .networkResources(List.of(11))
                .build();
        // when
        mockMvc.perform(
                        put(NAC_ROLE_CONTROLLER_URL)
                                .content(
                                        objectMapper.writeValueAsString(
                                                NacRolesModifyRequest.builder()
                                                        .roles(List.of(uNacRoleInsertDto1))
                                                        .build()
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());

    }
    @Test
    void givenNacRoleWithNotExistingNetworkResource_WhenUpdateRole_ThenStatus400() throws Exception {
        // given
        //insert role
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(11).name("r1").build());

        NacRoleInsertDto nacRoleInsertDto1 = NacRoleInsertDto.builder()
                .name("role1")
                .networkResources(List.of(11))
                .build();
        List<NacRoleInsertDto> insertRoles = List.of(nacRoleInsertDto1);

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder()
                .roles(insertRoles).build();

        List<NacRoleDto> vals = insertRolesAndGetSorted(nacRolesInsertRequest);

        // update role
        NacRoleModifyDto uNacRoleInsertDto1 = NacRoleModifyDto.builder()
                .id(vals.get(0).getId())
                .name(vals.get(0).getName())
                //                            not existing port
                .networkResources(List.of(11, 1999))
                .build();
        // when
        mockMvc.perform(put(NAC_ROLE_CONTROLLER_URL)
                                .content(
                                        objectMapper.writeValueAsString(
                                                NacRolesModifyRequest.builder()
                                                        .roles(List.of(uNacRoleInsertDto1))
                                                        .build()
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
    }
    // repeated role
    @Test
    void givenNacRole_WhenUpdateRolesWithDuplicateRoleId_ThenStatus400() throws Exception {
        // given
        //insert role
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(11).name("r1").build());
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(12).name("r2").build());

        NacRoleInsertDto nacRoleInsertDto1 = NacRoleInsertDto.builder()
                .name("role1")
                .networkResources(List.of(11))
                .build();
        List<NacRoleInsertDto> insertRoles = List.of(nacRoleInsertDto1);

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder()
                .roles(insertRoles).build();

        List<NacRoleDto> vals = insertRolesAndGetSorted(nacRolesInsertRequest);

        // update role
        NacRoleModifyDto uNacRoleInsertDto1 = NacRoleModifyDto.builder()
                .id(vals.get(0).getId())
                .name("first")
                .networkResources(List.of(11))
                .build();
        NacRoleModifyDto uNacRoleInsertDto2 = NacRoleModifyDto.builder()
                .id(vals.get(0).getId())
                .name("duplicate")
                .networkResources(List.of(12))
                .build();
        // when
        mockMvc.perform(put(NAC_ROLE_CONTROLLER_URL)
                                .content(
                                        objectMapper.writeValueAsString(
                                                NacRolesModifyRequest.builder()
                                                        .roles(List.of(uNacRoleInsertDto1, uNacRoleInsertDto2))
                                                        .build()
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
    }
    // repeated port
    @Test
    void givenNacRole_WhenUpdateRolesWithDuplicateNetworkResource_ThenStatus400() throws Exception {
        // given
        //insert role
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(11).name("r1").build());
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(12).name("r2").build());

        NacRoleInsertDto nacRoleInsertDto1 = NacRoleInsertDto.builder()
                .name("role1")
                .networkResources(List.of(11))
                .build();
        List<NacRoleInsertDto> insertRoles = List.of(nacRoleInsertDto1);

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder()
                .roles(insertRoles).build();

        List<NacRoleDto> vals = insertRolesAndGetSorted(nacRolesInsertRequest);

        // update role
        NacRoleModifyDto uNacRoleInsertDto1 = NacRoleModifyDto.builder()
                .id(vals.get(0).getId())
                .name(vals.get(0).getName())
                //                              repeat
                .networkResources(List.of(11, 12, 11))
                .build();
        // when
        mockMvc.perform(
                        put(NAC_ROLE_CONTROLLER_URL)
                                .content(
                                        objectMapper.writeValueAsString(
                                                NacRolesModifyRequest.builder()
                                                        .roles(List.of(uNacRoleInsertDto1))
                                                        .build()
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
    }
    // GET_ROLES SECTOR
    @Test
    void givenNacRoles_WhenPostRoleAndGetByName_ThenStatus200AndCorrectData() throws Exception {
        // given
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(11).name("r1").build());
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(1000).name("r2").build());

        List<Integer> networkResourceDtos1 = new ArrayList<>();
        networkResourceDtos1.add(11);
        networkResourceDtos1.add(1000);

        NacRoleInsertDto insertedRole = NacRoleInsertDto.builder()
                .name("role1")
                .networkResources(networkResourceDtos1)
                .build();

        List<NacRoleInsertDto> roles = List.of(insertedRole);

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder()
                .roles(roles).build();
        // when - then
        insertRoles(nacRolesInsertRequest);

        MvcResult mvcResult = mockMvc.perform(
                        get(NAC_ROLE_CONTROLLER_URL + "/role1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // сортируем для правильности проверки, т.к. ручка может вернуть ресурсы в любом порядке
        NacRoleDto actual = objectMapper.readValue(mapStringBody(mvcResult), NacRoleDto.class);
        if (actual.getNetworkResources() != null)
            actual.getNetworkResources().sort(Comparator.comparingInt(NetworkResourceDto::getResourcePort));

        roleInsertCompare(insertedRole, actual);
    }

    @Test
    void whenGetByNameNotExistingRole_ThenStatus404() throws Exception {
        mockMvc.perform(
                        get(NAC_ROLE_CONTROLLER_URL + "/role1"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    // DELETE_ROLES SECTOR
    @Test
    void givenNacRole_WhenDeleteRoleAndGetByName_ThenStatus404() throws Exception {
        // given
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(11).name("r1").build());
        networkResourcesRepository.save(NetworkResourceEntity.builder().resourcePort(1000).name("r2").build());

        NacRoleInsertDto insertedRole = NacRoleInsertDto.builder()
                .name("role1")
                .networkResources(List.of(11, 1000))
                .build();

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder()
                .roles(List.of(insertedRole)).build();
        insertRoles(nacRolesInsertRequest);
        // when
        mockMvc.perform(
                        delete(NAC_ROLE_CONTROLLER_URL + "/role1"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        get(NAC_ROLE_CONTROLLER_URL + "/role1"))
                // then
                .andExpect(status().isNotFound());
    }

    private List<NacRoleDto> insertRolesAndGetSorted(NacRolesInsertRequest nacRolesInsertRequest) throws Exception {
        insertRoles(nacRolesInsertRequest);
        MvcResult mvcResult = mockMvc.perform(
                        get(NAC_ROLE_CONTROLLER_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return objectMapper.readValue(mapStringBody(mvcResult), NacRolesResponse.class).getRoles();
    }

    private void insertRoles(NacRolesInsertRequest nacRolesInsertRequest) throws Exception {
        mockMvc.perform(
                        post(NAC_ROLE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(nacRolesInsertRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(result ->
                        Assertions.assertEquals("Вставка ролей прошла успешно!", mapStringBody(result)))
                .andReturn();
    }

    protected void sortNacRoleObjects(List<NacRoleDto> dtos) {
        if (dtos == null || dtos.size() == 0) return;

        for (NacRoleDto dto : dtos) {
            if (dto.getNetworkResources() != null)
                dto.getNetworkResources().sort(Comparator.comparingInt(NetworkResourceDto::getResourcePort));
        }
        dtos.sort(Comparator.comparing(NacRoleDto::getName));
    }

    protected void roleInsertCompareList(List<NacRoleInsertDto> expectedRoleDtos, List<NacRoleDto> actualInsertDtos) {
        assertEquals(expectedRoleDtos.size(), actualInsertDtos.size());

        for (int i = 0; i < expectedRoleDtos.size(); i++) {
            NacRoleDto actualRole = actualInsertDtos.get(i);
            NacRoleInsertDto expectedRole = expectedRoleDtos.get(i);

            roleInsertCompare(expectedRole, actualRole);
        }
    }

    protected void roleInsertCompare(NacRoleInsertDto expectedRoleDto, NacRoleDto actualInsertDto) {
        assertEquals(expectedRoleDto.getName(), actualInsertDto.getName());

        List<Integer> actualRolePorts = actualInsertDto.getNetworkResources().stream()
                .map(NetworkResourceDto::getResourcePort)
                .collect(Collectors.toList());

        assertEquals(expectedRoleDto.getNetworkResources(), actualRolePorts);

    }

    protected void roleModifyCompareList(List<NacRoleModifyDto> expectedRoleDtos, List<NacRoleDto> actualInsertDtos) {
        assertEquals(expectedRoleDtos.size(), actualInsertDtos.size());

        for (int i = 0; i < expectedRoleDtos.size(); i++) {
            NacRoleDto actualRole = actualInsertDtos.get(i);
            NacRoleModifyDto expectedRole = expectedRoleDtos.get(i);

            roleModifyCompare(expectedRole, actualRole);
        }
    }

    protected void roleModifyCompare(NacRoleModifyDto expectedRoleDto, NacRoleDto actualInsertDto) {
        assertEquals(expectedRoleDto.getName(), actualInsertDto.getName());

        List<Integer> actualRolePorts = actualInsertDto.getNetworkResources().stream()
                .map(NetworkResourceDto::getResourcePort)
                .collect(Collectors.toList());

        assertEquals(expectedRoleDto.getNetworkResources(), actualRolePorts);
    }
}
