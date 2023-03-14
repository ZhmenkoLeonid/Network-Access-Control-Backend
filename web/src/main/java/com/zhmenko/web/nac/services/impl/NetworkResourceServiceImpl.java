package com.zhmenko.web.nac.services.impl;

import com.zhmenko.ids.data.nac.entity.NacRoleEntity;
import com.zhmenko.ids.data.nac.entity.NetworkResourceEntity;
import com.zhmenko.ids.data.nac.entity.UserDeviceEntity;
import com.zhmenko.ids.data.security.entity.SecurityUserEntity;
import com.zhmenko.ids.models.ids.device.NetflowDeviceList;
import com.zhmenko.web.nac.data.repository.NetworkResourcesRepository;
import com.zhmenko.web.nac.exceptions.BadRequestException;
import com.zhmenko.web.nac.mapper.networkresource.NetworkResourceListMapper;
import com.zhmenko.web.nac.mapper.networkresource.NetworkResourceMapper;
import com.zhmenko.web.nac.model.NetworkResourceDto;
import com.zhmenko.web.nac.services.NetworkResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NetworkResourceServiceImpl implements NetworkResourceService {
    private final NetworkResourcesRepository networkResourcesRepository;
    private final NetworkResourceMapper networkResourceMapper;

    private final NetworkResourceListMapper networkResourceListMapper;

    private final NetflowDeviceList netflowDeviceList;

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
    @Transactional
    // TODO NOT WORKING. NEED TO FETCH RESOURCES BFR UPDATE
    public void updateNetworkResources(List<NetworkResourceDto> networkResourceDtos) {
        if (networkResourceDtos.size() == 0) return;

        // Проверяем, чтобы ресурсы с таким портом в бд существовали
        for (NetworkResourceDto networkResourceDto : networkResourceDtos) {
            if (!networkResourcesRepository.existsById(networkResourceDto.getResourcePort()))
                throw new BadRequestException("Ошибка валидации! Ресурса с портом \""
                        + networkResourceDto.getResourcePort() + "\" не существует");
        }

        duplicatePortsCheck(networkResourceDtos);

        networkResourcesRepository.saveAll(
                networkResourceListMapper.networkResourcesToNetworkResourceEntities(networkResourceDtos)
        );
    }

    @Override
    public void updateNetworkResource(NetworkResourceDto networkResource) {
        Optional<NetworkResourceEntity> oldResourceOpt = networkResourcesRepository.findById(networkResource.getResourcePort());
        if (oldResourceOpt.isEmpty())
            throw new BadRequestException("Ошибка валидации! Ресурса с портом \""
                    + networkResource.getResourcePort() + "\" не существует");

        NetworkResourceEntity networkResourceEntity = oldResourceOpt.get();

        networkResourceEntity.setName(networkResource.getName());

        networkResourcesRepository.save(networkResourceEntity);
    }

    @Override
    @Transactional
    public boolean deleteNetworkResourceByPort(int port) {
        Optional<NetworkResourceEntity> resourceEntityOptional = networkResourcesRepository.findById(port);
        if (resourceEntityOptional.isPresent()) {
            NetworkResourceEntity networkResourceEntity = resourceEntityOptional.get();
            Set<NacRoleEntity> rolesForUpdate = networkResourceEntity.getNacRoleEntities();

            // обновляем порты всех задетых устройств
            for (NacRoleEntity roleEntity : rolesForUpdate) {
                roleEntity.getNetworkResources().remove(networkResourceEntity);
                updateRoleLinkedSecurityUsersLocalPorts(roleEntity);
            }

            networkResourcesRepository.delete(networkResourceEntity);
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


    // обновляем порты всех устройств, у которых поменялась роль
    private void updateRoleLinkedSecurityUsersLocalPorts(NacRoleEntity nacRoleEntity) {
        // Для всех клиентов, у которых есть изменяемая роль,
        Set<SecurityUserEntity> roleOwners = nacRoleEntity.getRoleOwners();

        for (SecurityUserEntity roleOwner : roleOwners) {
            Set<Integer> newPorts = roleOwner.getPorts();
            //  у устройств меняем локальный список портов и acl на актуальные
            List<String> userDevicesMacAddresses = roleOwner.getNacUserEntities().stream()
                    .map(UserDeviceEntity::getMacAddress)
                    .collect(Collectors.toList());
            netflowDeviceList.updateLocalUserDevicesPortsByMacAddress(userDevicesMacAddresses, newPorts);
        }
    }
}
