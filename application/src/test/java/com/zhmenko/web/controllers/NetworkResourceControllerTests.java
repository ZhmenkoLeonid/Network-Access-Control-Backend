package com.zhmenko.web.controllers;

import com.zhmenko.ids.data.nac.entity.NetworkResourceEntity;
import com.zhmenko.web.nac.data.repository.NetworkResourcesRepository;
import com.zhmenko.web.nac.mapper.networkresource.NetworkResourceListMapper;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import com.zhmenko.web.nac.model.networkresource.request.NetworkResourceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.zhmenko.entity.NetworkResourceEntityTestBuilder.aNetworkResourceEntity;
import static com.zhmenko.web.controllers.util.StringUtils.mapStringBody;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NetworkResourceControllerTests extends AbstractTest {
    private final String NETWORK_RESOURCE_CONTROLLER_URL = baseApiUrl + "/network-resource";
    @Autowired
    private NetworkResourceListMapper networkResourceListMapper;
    @Autowired
    private NetworkResourcesRepository networkResourcesRepository;

    // IMPORT SECTOR
    @Test
    void givenResources_WhenPostResources_ThenStatus201AndCorrectData() throws Exception {
        // given
        NetworkResourceDto r1 = NetworkResourceDto.builder().resourcePort(1).name("r1").build();
        NetworkResourceDto r2 = NetworkResourceDto.builder().resourcePort(2).name("r2").build();
        NetworkResourceDto r3 = NetworkResourceDto.builder().resourcePort(3).name("r3").build();
        var resources = List.of(r1, r2, r3);
        NetworkResourceRequest request = NetworkResourceRequest.builder()
                .resources(resources)
                .build();
        // when - then
        mockMvc.perform(
                        post(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        List<NetworkResourceEntity> all = networkResourcesRepository.findAll();
        assertEquals(3, all.size());

        for (int i = 0; i < all.size(); i++) {
            var actual = all.get(i);
            var expected = resources.get(i);
            assertEquals(expected.getResourcePort(), actual.getResourcePort());
            assertEquals(expected.getName(), actual.getName());
        }
    }

    @Test
    void givenResource_WhenPostResourceWithBadPort_ThenStatus400() throws Exception {
        // given
        NetworkResourceDto negativePortResource = NetworkResourceDto.builder().resourcePort(-1).name("r1").build();
        NetworkResourceDto tooMuchPortResource = NetworkResourceDto.builder().resourcePort(65536).name("r2").build();

        NetworkResourceRequest negativePortRequest = NetworkResourceRequest.builder()
                .resources(List.of(negativePortResource))
                .build();
        NetworkResourceRequest tooMuchPortRequest = NetworkResourceRequest.builder()
                .resources(List.of(tooMuchPortResource))
                .build();

        // when - then
        mockMvc.perform(
                        post(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(negativePortRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(tooMuchPortRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenResource_WhenPostExistingResource_ThenStatus400() throws Exception {
        // given
        db.save(aNetworkResourceEntity().withPort(1).withName("r1"));
        // when
        NetworkResourceDto resourceDto = NetworkResourceDto.builder().resourcePort(1).name("r2").build();
        mockMvc.perform(
                        post(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(
                                                NetworkResourceRequest.builder().resources(List.of(resourceDto)).build()
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenResources_WhenPostResourcesWithDuplicatedPort_ThenStatus400() throws Exception {
        // given
        NetworkResourceDto r1 = NetworkResourceDto.builder().resourcePort(1).name("r1").build();
        NetworkResourceDto r2 = NetworkResourceDto.builder().resourcePort(1).name("r2").build();
        NetworkResourceDto r3 = NetworkResourceDto.builder().resourcePort(3).name("r3").build();

        NetworkResourceRequest request = NetworkResourceRequest.builder()
                .resources(List.of(r1, r2, r3))
                .build();
        // when
        mockMvc.perform(
                        post(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
    }

    // UPDATE SECTOR
    @Test
    void givenResource_WhenUpdateResource_ThenStatus200AndCorrectData() throws Exception {
        // given
        db.save(aNetworkResourceEntity().withPort(1).withName("r1"));
        // when
        var dto = NetworkResourceDto.builder().resourcePort(1).name("updated r1").build();
        mockMvc.perform(
                        put(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var actual = db.find(1, NetworkResourceEntity.class);
        assertNotNull(actual);

        assertEquals(dto.getName(), actual.getName());
    }

    @Test
    void givenResource_WhenUpdateResourceWithBadPort_ThenStatus400() throws Exception {
        // given
        db.save(aNetworkResourceEntity().withName("r1").withPort(1));
        // prepare updates
        NetworkResourceDto negativePortResource = NetworkResourceDto.builder().resourcePort(-1).name("r1").build();
        NetworkResourceDto tooMuchPortResource = NetworkResourceDto.builder().resourcePort(65536).name("r1").build();

        NetworkResourceRequest negativePortRequest = NetworkResourceRequest.builder()
                .resources(List.of(negativePortResource))
                .build();
        NetworkResourceRequest tooMuchPortRequest = NetworkResourceRequest.builder()
                .resources(List.of(tooMuchPortResource))
                .build();
        // when - then
        mockMvc.perform(
                        put(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(negativePortRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        put(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(tooMuchPortRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

/*    @Test
    void givenResources_WhenUpdateResourcesWithDuplicatedPort_ThenStatus400() throws Exception {
        // given
        NetworkResourceDto r1 = NetworkResourceDto.builder().resourcePort(1).name("r1").build();
        NetworkResourceDto r2 = NetworkResourceDto.builder().resourcePort(2).name("r2").build();
        NetworkResourceDto r3 = NetworkResourceDto.builder().resourcePort(3).name("r3").build();

        NetworkResourceRequest request = NetworkResourceRequest.builder()
                .resources(List.of(r1, r2, r3))
                .build();

        insertResources(request);
        // when
        // make duplicate
        r2.setResourcePort(1);
        mockMvc.perform(
                        post(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
    }*/

    // GET SECTOR
    @Test
    void givenNacRoles_WhenPostRoleAndGetByName_ThenStatus200AndCorrectData() throws Exception {
        // given
        db.save(aNetworkResourceEntity().withName("r1").withPort(1));
        NetworkResourceDto expected = NetworkResourceDto.builder().resourcePort(1).name("r1").build();

        MvcResult mvcResult = mockMvc.perform(
                        get(NETWORK_RESOURCE_CONTROLLER_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        NetworkResourceDto response = objectMapper
                .readValue(mapStringBody(mvcResult), NetworkResourceDto.class);

        assertEquals(expected, response);
    }

    @Test
    void whenGetByPortNotExistingResource_ThenStatus404() throws Exception {
        mockMvc.perform(
                        get(NETWORK_RESOURCE_CONTROLLER_URL
                                + "/1"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    // DELETE SECTOR
    @Test
    void givenNetworkResource_WhenDeleteResource_ThenStatus200AndResourceShouldBeDeleted() throws Exception {
        // given
        db.save(aNetworkResourceEntity().withPort(1).withName("r1"));
        // when
        assertNotNull(db.find(1, NetworkResourceEntity.class));
        mockMvc.perform(
                        delete(NETWORK_RESOURCE_CONTROLLER_URL + "/1"))
                .andExpect(status().isOk());
        // then
        assertNull(db.find(1, NetworkResourceEntity.class));
    }
}
