package com.zhmenko.web.security.mapper.securityrole;

import com.zhmenko.ids.data.security.entity.SecurityUserEntity;
import com.zhmenko.web.security.model.securityusercontroller.SecurityUserDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = SecurityRoleMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SecurityRoleListMapper {
    List<SecurityUserDto> securityUserEntitiesToSecurityUserDtos(Iterable<SecurityUserEntity> securityUserEntities);

    List<SecurityUserEntity> securityUserDtosToSecurityUserEntities(Iterable<SecurityUserDto> securityUserDtos);
}
