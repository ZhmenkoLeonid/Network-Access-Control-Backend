package com.zhmenko.web.nac.model.nacrole.request.modify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class NacRolesModifyRequest {
    @JsonProperty("roles")
    @NotNull
    private List<NacRoleModifyDto> roles;
}
