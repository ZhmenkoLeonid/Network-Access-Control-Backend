package com.zhmenko.web.nac.mapper;

import com.zhmenko.data.nac.models.NacRoleEntity;
import com.zhmenko.web.nac.model.nacrole.request.modify.NacRoleModifyDto;
import com.zhmenko.web.nac.model.nacrole.request.insert.NacRoleInsertDto;
import com.zhmenko.web.nac.model.nacrole.response.NacRoleDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NacRoleMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NacRoleListMapper {
    List<NacRoleEntity> nacRoleDtosToNacRoleEntities(List<NacRoleDto> nacRoleDtos);

    List<NacRoleDto> nacRoleEntitiesToNacRoleDtos(List<NacRoleEntity> nacRoleEntities);

    List<NacRoleEntity> nacRoleInsertDtosToNacRoleEntities(List<NacRoleInsertDto> nacRoleInsertDtos);

    List<NacRoleInsertDto> nacRoleEntitiesToNacRoleInsertDtos(List<NacRoleEntity> nacRoleEntities);

    List<NacRoleEntity> nacRoleModifyDtosToNacRoleEntities(List<NacRoleModifyDto> nacRoleModifyDtos);

    List<NacRoleModifyDto> nacRoleEntitiesToNacRoleModifyDtos(List<NacRoleEntity> nacRoleEntities);
}
