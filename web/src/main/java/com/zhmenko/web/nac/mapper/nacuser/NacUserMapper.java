package com.zhmenko.web.nac.mapper.nacuser;

import com.zhmenko.ids.data.nac.entity.UserDeviceEntity;
import com.zhmenko.web.nac.mapper.nacrole.NacRoleMapper;
import com.zhmenko.web.nac.model.user_device.UserDeviceDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = NacRoleMapper.class)
public interface NacUserMapper {
    UserDeviceEntity nacUserDtoToNacUserEntity(UserDeviceDto userDeviceDto);

    UserDeviceDto nacUserEntityToNacUserDto(UserDeviceEntity userDeviceEntity);
}
