package com.zhmenko.web.nac.services;

import com.zhmenko.web.nac.model.NetworkResourceDto;

import java.util.List;
import java.util.Optional;

public interface NetworkResourceService {
    void addNetworkResources(List<NetworkResourceDto> roles);

    void updateNetworkResources(List<NetworkResourceDto> roles);

    boolean deleteNetworkResourceByPort(int port);

    Optional<NetworkResourceDto> findByPort(int port);

    List<NetworkResourceDto> findAll();
}
