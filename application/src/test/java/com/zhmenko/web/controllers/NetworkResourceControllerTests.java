package com.zhmenko.web.controllers;

import com.zhmenko.web.nac.model.NetworkResourceDto;
import com.zhmenko.web.nac.model.networkresource.request.NetworkResourceRequest;
import com.zhmenko.web.nac.model.networkresource.response.NetworkResourceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Comparator;
import java.util.List;

import static com.zhmenko.web.controllers.util.StringUtils.mapStringBody;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NetworkResourceControllerTests extends AbstractTest {
    private final String NETWORK_RESOURCE_CONTROLLER_URL = baseApiUrl + "/network-resource";

    // IMPORT SECTOR
    @Test
    void givenResources_WhenPostResources_ThenStatus201AndCorrectData() throws Exception {
        // given
        NetworkResourceDto r1 = NetworkResourceDto.builder().resourcePort(1).name("r1").build();
        NetworkResourceDto r2 = NetworkResourceDto.builder().resourcePort(2).name("r2").build();
        NetworkResourceDto r3 = NetworkResourceDto.builder().resourcePort(3).name("r3").build();

        NetworkResourceRequest request = NetworkResourceRequest.builder()
                .resources(List.of(r1, r2, r3))
                .build();
        // when - then
        List<NetworkResourceDto> actual = insertResourcesAndGetSorted(request);

        assertEquals(request.getResources(), actual);
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
        NetworkResourceDto resourceDto = NetworkResourceDto.builder().resourcePort(1).name("r1").build();

        //create resource
        insertResources(NetworkResourceRequest.builder().resources(List.of(resourceDto)).build());

        // when
        resourceDto.setName("r2");

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
    void givenResources_WhenUpdateResources_ThenStatus200AndCorrectData() throws Exception {
        // given
        NetworkResourceDto r1 = NetworkResourceDto.builder().resourcePort(1).name("r1").build();
        NetworkResourceDto r2 = NetworkResourceDto.builder().resourcePort(2).name("r2").build();
        NetworkResourceDto r3 = NetworkResourceDto.builder().resourcePort(3).name("r3").build();

        NetworkResourceRequest request = NetworkResourceRequest.builder()
                .resources(List.of(r1, r2, r3))
                .build();

        insertResources(request);
        // when
        r1.setName("updated r1");
        r2.setName("updated r2");
        r3.setName("updated r3");

        mockMvc.perform(
                        put(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(
                        get(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<NetworkResourceDto> response = objectMapper
                .readValue(mapStringBody(mvcResult), NetworkResourceResponse.class).getResources();

        // sort by port num
        response.sort(Comparator.comparingInt(NetworkResourceDto::getResourcePort));

        assertEquals(request.getResources(), response);
    }
    @Test
    void givenResource_WhenUpdateResourceWithBadPort_ThenStatus400() throws Exception {
        // given
        NetworkResourceDto r1 = NetworkResourceDto.builder().resourcePort(1).name("r1").build();

        // insert
        insertResources(NetworkResourceRequest.builder().resources(List.of(r1)).build());

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
    @Test
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
    }

    // GET SECTOR
    @Test
    void givenNacRoles_WhenPostRoleAndGetByName_ThenStatus200AndCorrectData() throws Exception {
        // given
        NetworkResourceDto r1 = NetworkResourceDto.builder().resourcePort(1).name("r1").build();
        NetworkResourceRequest request = NetworkResourceRequest.builder().resources(List.of(r1)).build();

        // when - then
        insertResources(request);

        MvcResult mvcResult = mockMvc.perform(
                        get(NETWORK_RESOURCE_CONTROLLER_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        NetworkResourceDto response = objectMapper
                .readValue(mapStringBody(mvcResult), NetworkResourceDto.class);

        assertEquals(r1, response);
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
    void givenNetwork_WhenDeleteResourceAndGetByName_ThenStatus404() throws Exception {
        // given
        NetworkResourceDto r1 = NetworkResourceDto.builder().resourcePort(1).name("r1").build();
        NetworkResourceRequest request = NetworkResourceRequest.builder().resources(List.of(r1)).build();
        insertResources(request);
        // when
        mockMvc.perform(
                        delete(NETWORK_RESOURCE_CONTROLLER_URL + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        get(NETWORK_RESOURCE_CONTROLLER_URL + "/1"))
                // then
                .andExpect(status().isNotFound());
    }
    void insertResources(NetworkResourceRequest networkResourceRequest) throws Exception {
        mockMvc.perform(
                        post(NETWORK_RESOURCE_CONTROLLER_URL)
                                .content(objectMapper.writeValueAsString(networkResourceRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    List<NetworkResourceDto> insertResourcesAndGetSorted(NetworkResourceRequest networkResourceRequest)
            throws Exception {
        insertResources(networkResourceRequest);

        MvcResult mvcResult = mockMvc.perform(
                        get(NETWORK_RESOURCE_CONTROLLER_URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<NetworkResourceDto> response = objectMapper
                .readValue(mapStringBody(mvcResult), NetworkResourceResponse.class).getResources();

        // sort by port num
        response.sort(Comparator.comparingInt(NetworkResourceDto::getResourcePort));
        return response;
    }
}
