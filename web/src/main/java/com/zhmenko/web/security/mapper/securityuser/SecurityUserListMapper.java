package com.zhmenko.web.security.mapper.securityuser;

import com.zhmenko.ids.data.security.entity.SecurityUserEntity;
import com.zhmenko.web.security.model.securityusercontroller.SecurityUserDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = SecurityUserMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SecurityUserListMapper {
    List<SecurityUserDto> securityUserEntitiesToSecurityUserDtos(List<SecurityUserEntity> securityUserEntities);

    List<SecurityUserEntity> securityUserDtosToSecurityUserEntities(List<SecurityUserDto> securityUserDtos);
}
