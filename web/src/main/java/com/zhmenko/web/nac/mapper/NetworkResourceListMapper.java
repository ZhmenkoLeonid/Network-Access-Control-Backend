package com.zhmenko.web.nac.mapper;

import com.zhmenko.data.nac.models.NetworkResourceEntity;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = NetworkResourceMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NetworkResourceListMapper {
    List<NetworkResourceEntity> networkResourcesToNetworkResourceEntities(List<NetworkResourceDto> networkResourceDtos);

    List<NetworkResourceDto> networkResourceEntitiesToNetworkResources(List<NetworkResourceEntity> networkResourceEntities);
}
