package com.zhmenko.web.security.model.securityusercontroller.response;

import com.zhmenko.web.security.model.securityusercontroller.SecurityUserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityUserResponse {
    private List<SecurityUserDto> securityUsersList;
}
