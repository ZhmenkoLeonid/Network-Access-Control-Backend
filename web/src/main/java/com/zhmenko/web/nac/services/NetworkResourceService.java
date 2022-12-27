package com.zhmenko.web.nac.services;

import com.zhmenko.web.nac.model.NetworkResourceDto;

import java.util.List;
import java.util.Optional;

public interface NetworkResourceService {
    void addNetworkResources(List<NetworkResourceDto> networkResources);

    void updateNetworkResources(List<NetworkResourceDto> networkResources);

    void updateNetworkResource(NetworkResourceDto networkResource);

    boolean deleteNetworkResourceByPort(int port);

    Optional<NetworkResourceDto> findByPort(int port);

    List<NetworkResourceDto> findAll();
}
