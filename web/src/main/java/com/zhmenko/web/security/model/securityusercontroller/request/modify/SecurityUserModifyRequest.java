package com.zhmenko.web.security.model.securityusercontroller.request.modify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUserModifyRequest {
    private List<SecurityUserModifyDto> securityUsers;
}
