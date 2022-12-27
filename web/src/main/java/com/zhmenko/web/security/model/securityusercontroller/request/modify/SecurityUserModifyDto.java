package com.zhmenko.web.security.model.securityusercontroller.request.modify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityUserModifyDto {
    @NotNull
    private UUID id;

    @NotBlank
    private String username;

    @NotNull
    @UniqueElements
    private Set<Long> securityRoles;

    @NotNull
    @UniqueElements
    private Set<Long> nacRoles;
}
