package com.zhmenko.web.security.mapper.securityuser;

import com.zhmenko.ids.data.security.entity.SecurityUserEntity;
import com.zhmenko.web.nac.mapper.nacuser.NacUserListMapper;
import com.zhmenko.web.security.mapper.securityrole.SecurityRoleListMapper;
import com.zhmenko.web.security.model.securityusercontroller.SecurityUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {NacUserListMapper.class, SecurityRoleListMapper.class})
public interface SecurityUserMapper {
    @Mapping(source = "nacUserEntities", target = "nacUsers")
    SecurityUserDto securityUserEntityToSecurityUserDto(SecurityUserEntity securityUserEntity);

    @Mapping(target = "nacUserEntities", source = "nacUsers")
    SecurityUserEntity securityUserDtoToSecurityUserEntity(SecurityUserDto securityUserDto);
}
