package com.zhmenko.web.nac.model.networkresource.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkResourceRequest {
    @JsonProperty("resources")
    @NotNull
    @Valid
    private List<NetworkResourceDto> resources;
}
