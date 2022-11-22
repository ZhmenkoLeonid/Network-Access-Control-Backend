package com.zhmenko.web.nac.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkResourceDto {
    @JsonProperty("port")
    @Min(0)
    @Max(65535)
    private int resourcePort;

    @NotBlank
    @JsonProperty("name")
    private String name;
}
