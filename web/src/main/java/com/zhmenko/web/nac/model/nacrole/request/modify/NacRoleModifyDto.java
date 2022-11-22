package com.zhmenko.web.nac.model.nacrole.request.modify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NacRoleModifyDto {
    @NotNull
    @JsonProperty("id")
    private long id;

    @NotBlank
    @JsonProperty("name")
    protected String name;

    @JsonProperty("networkResources")
    protected List<Integer> networkResources;
}
