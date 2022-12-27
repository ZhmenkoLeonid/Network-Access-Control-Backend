package com.zhmenko.web.security.mapper.securityrole;

import com.zhmenko.data.security.models.SecurityUserEntity;
import com.zhmenko.web.security.model.securityusercontroller.SecurityUserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SecurityRoleMapper {
    SecurityUserDto securityUserEntityToSecurityUserDto(SecurityUserEntity securityUserEntity);

    SecurityUserEntity securityUserDtoToSecurityUserEntity(SecurityUserDto securityUserDto);
}
