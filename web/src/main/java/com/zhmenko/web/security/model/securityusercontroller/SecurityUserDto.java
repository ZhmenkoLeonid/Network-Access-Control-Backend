package com.zhmenko.web.security.model.securityusercontroller;

import com.zhmenko.web.nac.model.nacrole.response.NacRoleDto;
import com.zhmenko.web.nac.model.user_device.UserDeviceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityUserDto {
    @NotNull
    private UUID id;
    @NotBlank
    private String username;

    private Set<SecurityRoleDto> securityRoles;

    private Set<UserDeviceDto> nacUsers;

    private Set<NacRoleDto> nacRoles;
}
