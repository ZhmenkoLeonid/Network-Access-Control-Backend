package com.zhmenko.web.security.services.impl;

import com.zhmenko.ids.data.nac.entity.NacRoleEntity;
import com.zhmenko.ids.data.nac.entity.NetworkResourceEntity;
import com.zhmenko.ids.data.nac.entity.UserDeviceEntity;
import com.zhmenko.ids.data.security.entity.SecurityRoleEntity;
import com.zhmenko.ids.data.security.entity.SecurityUserEntity;
import com.zhmenko.ids.data.security.repository.SecurityUserRepository;
import com.zhmenko.ids.models.ids.exception.UserNotExistException;
import com.zhmenko.ids.models.ids.device.NetflowDeviceList;
import com.zhmenko.web.nac.data.repository.NacRoleRepository;
import com.zhmenko.web.nac.exceptions.BadRequestException;
import com.zhmenko.web.security.data.repository.SecurityRoleRepository;
import com.zhmenko.web.security.mapper.securityuser.SecurityUserListMapper;
import com.zhmenko.web.security.mapper.securityuser.SecurityUserMapper;
import com.zhmenko.web.security.model.securityusercontroller.SecurityUserDto;
import com.zhmenko.web.security.model.securityusercontroller.request.modify.SecurityUserModifyDto;
import com.zhmenko.web.security.services.SecurityUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityUserServiceImpl implements SecurityUserService {
    private final SecurityUserRepository securityUserRepository;
    private final SecurityRoleRepository securityRoleRepository;
    private final NacRoleRepository nacRoleRepository;
    private final NetflowDeviceList netflowDeviceList;
    private final SecurityUserMapper securityUserMapper;
    private final SecurityUserListMapper securityUserListMapper;

    @Override
    public List<SecurityUserDto> findAll() {
        return securityUserListMapper.securityUserEntitiesToSecurityUserDtos(securityUserRepository.findAll());
    }

    @Override
    public Optional<SecurityUserDto> findById(UUID id) {
        Optional<SecurityUserEntity> user = securityUserRepository.findById(id);
        return user.map(securityUserMapper::securityUserEntityToSecurityUserDto);
    }

    @Override
    @Transactional
    public void updateUser(SecurityUserModifyDto securityUserModifyDto) {
        SecurityUserEntity oldUser = securityUserRepository.findById(securityUserModifyDto.getId())
                .orElseThrow(() -> new UserNotExistException("Ошибка при обновлении: не найдено пользователя с id " + securityUserModifyDto.getId()));

        Set<Integer> permitPorts = new HashSet<>();
        Set<NacRoleEntity> updatedNacRoles = new HashSet<>();
        Set<SecurityRoleEntity> updatedSecurityRoles = new HashSet<>();
        for (Long roleId : securityUserModifyDto.getNacRoles()) {
            NacRoleEntity role = nacRoleRepository.findById(roleId)
                    .orElseThrow(() -> new BadRequestException("Ошибка валидации! Не найдена nac роль с id: " + roleId));
            log.info("found role:" + role.getName());
            permitPorts.addAll(role.getNetworkResources().stream()
                    .map(NetworkResourceEntity::getResourcePort)
                    .collect(Collectors.toList()));
            updatedNacRoles.add(role);
        }

        for (Long roleId : securityUserModifyDto.getSecurityRoles()) {
            SecurityRoleEntity role = securityRoleRepository.findById(roleId)
                    .orElseThrow(() -> new BadRequestException("Ошибка валидации! Не найдена security роль с id: " + roleId));
            updatedSecurityRoles.add(role);
        }

        oldUser.setUsername(securityUserModifyDto.getUsername());
        oldUser.setNacRoles(updatedNacRoles);
        oldUser.setSecurityRoles(updatedSecurityRoles);
        log.info("updating security user:" + oldUser);
        securityUserRepository.save(oldUser);

        log.info("users for update for uuid: " + oldUser.getId() + "; cnt=" + oldUser.getNacUserEntities().size());
        List<String> macAddressesForUpdate = oldUser.getNacUserEntities().stream()
                .map(UserDeviceEntity::getMacAddress)
                .collect(Collectors.toList());

        netflowDeviceList.updateLocalUserDevicesPortsByMacAddress(macAddressesForUpdate, permitPorts);
    }

    @Override
    @Transactional
    public boolean deleteUserById(UUID id) {
        Optional<SecurityUserEntity> securityUserOpt;
        if ((securityUserOpt = securityUserRepository.findById(id)).isPresent()) {
            SecurityUserEntity securityUser = securityUserOpt.get();
            // Удаляем локальные записи устройств, связанных с пользователем
            for (UserDeviceEntity userDeviceEntity : securityUser.getNacUserEntities()) {
                netflowDeviceList.deleteDevice(userDeviceEntity);
            }
            // удаляем из репозитория
            securityUserRepository.delete(securityUser);
            return true;
        }
        return false;
    }
}
