package com.zhmenko.web.nac.model.nacrole.request.insert;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NacRoleInsertDto {
    @NotBlank
    @JsonProperty("name")
    protected String name;

    @JsonProperty("networkResources")
    protected List<Integer> networkResources;
}
