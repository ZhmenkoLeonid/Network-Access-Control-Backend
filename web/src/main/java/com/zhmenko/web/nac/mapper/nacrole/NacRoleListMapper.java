package com.zhmenko.web.nac.mapper.nacrole;

import com.zhmenko.ids.data.nac.entity.NacRoleEntity;
import com.zhmenko.web.nac.model.nacrole.request.modify.NacRoleModifyDto;
import com.zhmenko.web.nac.model.nacrole.request.insert.NacRoleInsertDto;
import com.zhmenko.web.nac.model.nacrole.response.NacRoleDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NacRoleMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NacRoleListMapper {
    List<NacRoleEntity> nacRoleDtosToNacRoleEntities(Iterable<NacRoleDto> nacRoleDtos);

    List<NacRoleDto> nacRoleEntitiesToNacRoleDtos(Iterable<NacRoleEntity> nacRoleEntities);

    List<NacRoleEntity> nacRoleInsertDtosToNacRoleEntities(Iterable<NacRoleInsertDto> nacRoleInsertDtos);

    List<NacRoleInsertDto> nacRoleEntitiesToNacRoleInsertDtos(Iterable<NacRoleEntity> nacRoleEntities);

    List<NacRoleEntity> nacRoleModifyDtosToNacRoleEntities(Iterable<NacRoleModifyDto> nacRoleModifyDtos);

    List<NacRoleModifyDto> nacRoleEntitiesToNacRoleModifyDtos(Iterable<NacRoleEntity> nacRoleEntities);
}
