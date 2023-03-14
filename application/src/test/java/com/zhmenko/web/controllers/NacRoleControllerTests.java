package com.zhmenko.web.controllers;


import com.zhmenko.data.nac.entity.NacRoleEntity;
import com.zhmenko.data.nac.entity.NetworkResourceEntity;
import com.zhmenko.data.nac.repository.NacRoleRepository;
import com.zhmenko.entity.NacRoleEntityTestBuilder;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import com.zhmenko.web.nac.model.nacrole.request.insert.NacRoleInsertDto;
import com.zhmenko.web.nac.model.nacrole.request.insert.NacRolesInsertRequest;
import com.zhmenko.web.nac.model.nacrole.request.modify.NacRoleModifyDto;
import com.zhmenko.web.nac.model.nacrole.request.modify.NacRolesModifyRequest;
import com.zhmenko.web.nac.model.nacrole.response.NacRoleDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zhmenko.entity.NetworkResourceEntityTestBuilder.aNetworkResourceEntity;
import static com.zhmenko.web.controllers.util.StringUtils.mapStringBody;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
public class NacRoleControllerTests extends AbstractTest {
    @Autowired
    private NacRoleRepository nacRoleRepository;
    private final String NAC_ROLE_CONTROLLER_URL = baseApiUrl + "/nac-role";
    // ADD_ROLES SECTOR
    @Test
    void givenNacRoles_WhenPostRoles_ThenStatus201AndCorrectData() throws Exception {
        db.save(aNetworkResourceEntity().withPort(11).withName("r1"));
        db.save(aNetworkResourceEntity().withPort(1000).withName("r2"));
        db.save(aNetworkResourceEntity().withPort(10000).withName("r3"));

        NacRoleInsertDto nacRoleInsertDto1 = NacRoleInsertDto.builder()
                .name("role1")
                .networkResources(List.of(11,1000))
                .build();

        NacRoleInsertDto nacRoleInsertDto2 = NacRoleInsertDto.builder()
                .name("role2")
                .networkResources(List.of(10000))
                .build();
        List<NacRoleInsertDto> roles = List.of(nacRoleInsertDto1, nacRoleInsertDto2);

        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder()
                .roles(roles).build();

        mockMvc.perform(
                        post(NAC_ROLE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(nacRolesInsertRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(result ->
                        Assertions.assertEquals("Вставка ролей прошла успешно!", mapStringBody(result)))
                .andReturn();

        List<NacRoleEntity> all = nacRoleRepository.findAll();
        assertEquals(2, all.size());
        NacRoleEntity firstActual = all.get(0);
        NacRoleEntity secondActual = all.get(1);

        assertEquals(nacRoleInsertDto1.getName(), firstActual.getName());
        assertEquals(nacRoleInsertDto1.getNetworkResources(),
                firstActual.getNetworkResources().stream()
                        .map(NetworkResourceEntity::getResourcePort)
                        .sorted()
                        .collect(Collectors.toList()));

        assertEquals(nacRoleInsertDto2.getName(), secondActual.getName());
        assertEquals(nacRoleInsertDto2.getNetworkResources(),
                secondActual.getNetworkResources().stream()
                        .map(NetworkResourceEntity::getResourcePort)
                        .sorted()
                        .collect(Collectors.toList()));
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

        List<NacRoleEntity> all = nacRoleRepository.findAll();
        assertEquals(1, all.size());
        NacRoleEntity actual = all.get(0);

        assertEquals(nacRoleInsertDto.getName(), actual.getName());
        assertEquals(Collections.emptySet(), actual.getNetworkResources());
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
        db.save(NacRoleEntityTestBuilder.aNacRoleEntity().withName("role1"));
        NacRolesInsertRequest nacRolesInsertRequest = NacRolesInsertRequest.builder().roles(List.of(nacRoleInsertDto)).build();
        // when-then
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
        db.save(aNetworkResourceEntity().withPort(11).withName("r1"));

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
    void givenNacRoles_WhenUpdateRoles_ThenStatus200AndCorrectData() throws Exception {
        // given
        //insert
        NetworkResourceEntity r1 = aNetworkResourceEntity().withPort(11).withName("r1").build();
        NetworkResourceEntity r2 = aNetworkResourceEntity().withPort(12).withName("r2").build();
        NetworkResourceEntity r3 = aNetworkResourceEntity().withPort(13).withName("r2").build();
        NacRoleEntityTestBuilder role1 = NacRoleEntityTestBuilder.aNacRoleEntity()
                .withName("role1")
                .withNetworkResourceEntities(Set.of(r1,r2));
        NacRoleEntityTestBuilder role2 = NacRoleEntityTestBuilder.aNacRoleEntity()
                .withName("role2")
                .withNetworkResourceEntities(Set.of(r2,r3));
        db.save(r1);
        db.save(r2);
        db.save(r3);
        NacRoleEntity roleEntity1 = db.save(role1);
        NacRoleEntity roleEntity2 = db.save(role2);

        // update data
        NacRoleModifyDto uNacRoleInsertDto1 = NacRoleModifyDto.builder()
                .id(roleEntity1.getId())
                .name("updated role 1")
                .networkResources(List.of(13))
                .build();
        NacRoleModifyDto uNacRoleInsertDto2 = NacRoleModifyDto.builder()
                .id(roleEntity2.getId())
                .name("updated role 2")
                .networkResources(List.of(11,13))
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

        NacRoleEntity updatedRoleEntity1 = db.find(roleEntity1.getId(), NacRoleEntity.class);
        NacRoleEntity updatedRoleEntity2 = db.find(roleEntity2.getId(), NacRoleEntity.class);
        assertNotNull(updatedRoleEntity1);
        assertNotNull(updatedRoleEntity2);

        assertEquals("updated role 1",updatedRoleEntity1.getName());
        assertEquals(List.of(13), updatedRoleEntity1.getNetworkResources().stream()
                .map(NetworkResourceEntity::getResourcePort)
                .collect(Collectors.toList()));

        assertEquals("updated role 2",updatedRoleEntity2.getName());
        assertEquals(List.of(11, 13), updatedRoleEntity2.getNetworkResources().stream()
                .map(NetworkResourceEntity::getResourcePort)
                .sorted()
                .collect(Collectors.toList()));
    }
    @Test
    void givenNacRoleWithNotExistingId_WhenUpdateRole_ThenStatus400() throws Exception {
        // given
        db.save(aNetworkResourceEntity().withPort(11).withName("r1"));

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
        NetworkResourceEntity r1 = aNetworkResourceEntity().withPort(11).withName("r1").build();
        NacRoleEntityTestBuilder role1 = NacRoleEntityTestBuilder.aNacRoleEntity()
                .withName("role1")
                .withNetworkResourceEntities(Set.of(r1));
        db.save(r1);
        db.save(role1);

        // update role
        NacRoleModifyDto uNacRoleInsertDto1 = NacRoleModifyDto.builder()
                .id(1L)
                .name("role1")
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
        NetworkResourceEntity r1 = aNetworkResourceEntity().withPort(11).withName("r1").build();
        NetworkResourceEntity r2 = aNetworkResourceEntity().withPort(12).withName("r2").build();
        NacRoleEntityTestBuilder role1 = NacRoleEntityTestBuilder.aNacRoleEntity()
                .withName("role1")
                .withNetworkResourceEntities(Set.of(r1));
        db.save(r1);
        db.save(r2);
        NacRoleEntity nacRole = db.save(role1);

        // update role
        NacRoleModifyDto uNacRoleInsertDto1 = NacRoleModifyDto.builder()
                .id(nacRole.getId())
                .name("first")
                .networkResources(List.of(11))
                .build();
        NacRoleModifyDto uNacRoleInsertDto2 = NacRoleModifyDto.builder()
                .id(nacRole.getId())
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
        NetworkResourceEntity r1 = aNetworkResourceEntity().withPort(11).withName("r1").build();
        NetworkResourceEntity r2 = aNetworkResourceEntity().withPort(12).withName("r2").build();
        NacRoleEntityTestBuilder role1 = NacRoleEntityTestBuilder.aNacRoleEntity()
                .withName("role1")
                .withNetworkResourceEntities(Set.of(r1));
        db.save(r1);
        db.save(r2);
        NacRoleEntity nacRole = db.save(role1);

        // update role
        NacRoleModifyDto uNacRoleInsertDto1 = NacRoleModifyDto.builder()
                .id(nacRole.getId())
                .name(nacRole.getName())
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
        NetworkResourceEntity r1 = aNetworkResourceEntity().withPort(11).withName("r1").build();
        NetworkResourceEntity r2 = aNetworkResourceEntity().withPort(12).withName("r2").build();
        NacRoleEntityTestBuilder role1 = NacRoleEntityTestBuilder.aNacRoleEntity()
                .withName("role1")
                .withNetworkResourceEntities(Set.of(r1,r2));
        db.save(r1);
        db.save(r2);
        NacRoleEntity roleEntity = db.save(role1);

        MvcResult mvcResult = mockMvc.perform(
                        get(NAC_ROLE_CONTROLLER_URL + "/role1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // сортируем для правильности проверки, т.к. ручка может вернуть ресурсы в любом порядке
        NacRoleDto actual = objectMapper.readValue(mapStringBody(mvcResult), NacRoleDto.class);
        assertNotNull(actual.getNetworkResources());
        actual.getNetworkResources().sort(Comparator.comparingInt(NetworkResourceDto::getResourcePort));

        assertEquals(roleEntity.getName(), actual.getName());

        List<Integer> actualRolePorts = actual.getNetworkResources().stream()
                .map(NetworkResourceDto::getResourcePort)
                .collect(Collectors.toList());

        assertEquals(List.of(r1.getResourcePort(),r2.getResourcePort()), actualRolePorts);
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
    void givenNacRole_WhenDeleteRole_ThenStatus200AndRoleNotExist() throws Exception {
        NetworkResourceEntity r1 = aNetworkResourceEntity().withPort(11).withName("r1").build();
        NetworkResourceEntity r2 = aNetworkResourceEntity().withPort(12).withName("r2").build();
        NacRoleEntityTestBuilder role1 = NacRoleEntityTestBuilder.aNacRoleEntity()
                .withName("role1")
                .withNetworkResourceEntities(Set.of(r1,r2));
        db.save(r1);
        db.save(r2);
        NacRoleEntity roleEntity = db.save(role1);

        assertNotNull(db.find(roleEntity.getId(), NacRoleEntity.class));

        // when
        mockMvc.perform(
                        delete(NAC_ROLE_CONTROLLER_URL + "/role1"))
                .andExpect(status().isOk());


        assertNull(db.find(roleEntity.getId(), NacRoleEntity.class));
    }
}
