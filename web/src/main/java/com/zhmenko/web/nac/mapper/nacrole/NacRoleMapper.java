package com.zhmenko.web.nac.mapper.nacrole;

import com.zhmenko.ids.data.nac.entity.NacRoleEntity;
import com.zhmenko.web.nac.mapper.networkresource.NetworkResourceMapper;
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