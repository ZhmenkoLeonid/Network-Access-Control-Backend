package com.zhmenko.web.nac.model.nacrole.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NacRolesResponse {
    @JsonProperty("roles")
    private List<NacRoleDto> roles;
}
