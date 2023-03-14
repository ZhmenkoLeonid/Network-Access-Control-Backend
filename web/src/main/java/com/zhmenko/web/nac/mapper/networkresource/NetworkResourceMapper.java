package com.zhmenko.web.nac.mapper.networkresource;

import com.zhmenko.data.nac.entity.NetworkResourceEntity;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class NetworkResourceMapper {
    public abstract NetworkResourceEntity networkResourceDtoToNetworkResourceEntity(NetworkResourceDto networkResourceDto);

    public abstract NetworkResourceDto networkResourceEntityToNetworkResourceDto(NetworkResourceEntity networkResourceEntity);

    public NetworkResourceEntity portNumberToNetworkResourceEntity(Integer networkResourceDto) {
        return NetworkResourceEntity.builder().resourcePort(networkResourceDto).build();
    }

    public Integer networkResourceEntityToPortNumber(NetworkResourceEntity networkResourceEntity) {
        return networkResourceEntity.getResourcePort();
    }

}
