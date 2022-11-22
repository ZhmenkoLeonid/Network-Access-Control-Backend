package com.zhmenko.web.nac.model.nacrole.request.insert;

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
public class NacRolesInsertRequest {
    @JsonProperty("roles")
    @NotNull
    private List<NacRoleInsertDto> roles;
}
