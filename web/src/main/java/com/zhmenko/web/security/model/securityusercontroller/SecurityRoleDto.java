package com.zhmenko.web.security.model.securityusercontroller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityRoleDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
}
