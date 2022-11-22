package com.zhmenko.web.nac.mapper;

import com.zhmenko.data.nac.models.NacRoleEntity;
import com.zhmenko.web.nac.model.nacrole.request.modify.NacRoleModifyDto;
import com.zhmenko.web.nac.model.nacrole.request.insert.NacRoleInsertDto;
import com.zhmenko.web.nac.model.nacrole.response.NacRoleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = NetworkResourceMapper.class)
public interface NacRoleMapper {
    NacRoleEntity nacRoleInsertDtoToNacRoleEntity(NacRoleInsertDto nacRoleInsertDto);

    NacRoleInsertDto nacRoleEntityToNacRoleInsertDto(NacRoleEntity nacRoleEntity);

    NacRoleEntity nacRoleModifyDtoToNacRoleEntity(NacRoleModifyDto nacRoleInsertDto);

    NacRoleModifyDto nacRoleEntityToNacRoleModifyDto(NacRoleEntity nacRoleEntity);

    NacRoleEntity nacRoleDtoToNacRoleEntity(NacRoleDto nacRoleInsertDto);

    NacRoleDto nacRoleEntityToNacRoleDto(NacRoleEntity nacRoleEntity);
}