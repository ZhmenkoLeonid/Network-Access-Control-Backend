package com.zhmenko.web.nac.mapper.nacuser;

import com.zhmenko.ids.data.nac.entity.UserDeviceEntity;
import com.zhmenko.web.nac.model.user_device.UserDeviceDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NacUserMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NacUserListMapper {
    List<UserDeviceEntity> nacUserDtosToNacUserEntities(Iterable<UserDeviceDto> nacUserDtos);

    List<UserDeviceDto> nacUserEntitiesToNacUserDtos(Iterable<UserDeviceEntity> nacUserEntities);
}
