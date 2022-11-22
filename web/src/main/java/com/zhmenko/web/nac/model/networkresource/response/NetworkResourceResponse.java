package com.zhmenko.web.nac.model.networkresource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkResourceResponse {
    @JsonProperty("resources")
    private List<NetworkResourceDto> resources;
}