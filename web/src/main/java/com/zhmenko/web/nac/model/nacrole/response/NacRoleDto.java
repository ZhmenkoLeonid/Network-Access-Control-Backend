package com.zhmenko.web.nac.model.nacrole.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NacRoleDto {
    @NotNull
    @JsonProperty("id")
    private long id;

    @NotBlank
    @JsonProperty("name")
    protected String name;

    @JsonProperty("networkResources")
    protected List<NetworkResourceDto> networkResources;
}
