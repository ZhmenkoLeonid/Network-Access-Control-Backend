package com.zhmenko.web.nac.mapper.networkresource;

import com.zhmenko.data.nac.entity.NetworkResourceEntity;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NetworkResourceMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NetworkResourceListMapper {
    List<NetworkResourceEntity> networkResourcesToNetworkResourceEntities(Iterable<NetworkResourceDto> networkResourceDtos);

    List<NetworkResourceDto> networkResourceEntitiesToNetworkResources(Iterable<NetworkResourceEntity> networkResourceEntities);
}
