package com.zhmenko.web.nac.services.impl;

import com.zhmenko.data.nac.models.NetworkResourceEntity;
import com.zhmenko.data.nac.repository.NetworkResourcesRepository;
import com.zhmenko.web.nac.exceptions.BadRequestException;
import com.zhmenko.web.nac.mapper.NetworkResourceListMapper;
import com.zhmenko.web.nac.mapper.NetworkResourceMapper;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import com.zhmenko.web.nac.services.NetworkResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NetworkResourceServiceImpl implements NetworkResourceService {
    private final NetworkResourcesRepository networkResourcesRepository;

    private final NetworkResourceMapper networkResourceMapper;

    private final NetworkResourceListMapper networkResourceListMapper;

    @Override
    public void addNetworkResources(List<NetworkResourceDto> networkResourceDtos) {
        if (networkResourceDtos.size() == 0) return;

        // Проверяем, чтобы ресурсов с таким портом в бд не существовало
        for (NetworkResourceDto networkResourceDto : networkResourceDtos) {
            if (networkResourcesRepository.existsById(networkResourceDto.getResourcePort()))
                throw new BadRequestException("Ошибка валидации! Ресурс с портом \""
                        + networkResourceDto.getResourcePort() + "\" уже существует");
        }

        duplicatePortsCheck(networkResourceDtos);

        networkResourcesRepository.saveAll(
                networkResourceListMapper.networkResourcesToNetworkResourceEntities(networkResourceDtos)
        );
    }

    @Override
    public void updateNetworkResources(List<NetworkResourceDto> networkResourceDtos) {
        if (networkResourceDtos.size() == 0) return;

        // Проверяем, чтобы ресурсы с таким портом в бд существовали
        for (NetworkResourceDto networkResourceDto : networkResourceDtos) {
            if (!networkResourcesRepository.existsById(networkResourceDto.getResourcePort()))
                throw new BadRequestException("Ошибка валидации! Ресурс с портом \""
                        + networkResourceDto.getResourcePort() + "\" уже существует");
        }

        duplicatePortsCheck(networkResourceDtos);

        networkResourcesRepository.saveAll(
                networkResourceListMapper.networkResourcesToNetworkResourceEntities(networkResourceDtos)
        );
    }

    @Override
    public boolean deleteNetworkResourceByPort(int port) {
        if (networkResourcesRepository.existsById(port)) {
            networkResourcesRepository.deleteById(port);
            return true;
        }
        return false;
    }

    @Override
    public Optional<NetworkResourceDto> findByPort(int port) {
        Optional<NetworkResourceEntity> networkResourceEntity = networkResourcesRepository.findById(port);
        return networkResourceEntity.map(networkResourceMapper::networkResourceEntityToNetworkResourceDto);
    }

    @Override
    public List<NetworkResourceDto> findAll() {
        return networkResourceListMapper
                .networkResourceEntitiesToNetworkResources(networkResourcesRepository.findAll());
    }

    // Проверяем на наличие дубликатов по номеру порта
    private void duplicatePortsCheck(List<NetworkResourceDto> networkResourceDtos) {
        long uniquePortCnt = networkResourceDtos.stream()
                .map(NetworkResourceDto::getResourcePort)
                .distinct()
                .count();
        if (uniquePortCnt != networkResourceDtos.size())
            throw new BadRequestException(
                    "Ошибка валидации! В запросе присутствуют записи с одинаковыми портами");
    }
}
