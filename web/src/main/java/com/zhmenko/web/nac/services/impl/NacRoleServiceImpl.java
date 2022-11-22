package com.zhmenko.web.nac.services.impl;

import com.zhmenko.data.nac.models.NacRoleEntity;
import com.zhmenko.data.nac.repository.NacRoleRepository;
import com.zhmenko.data.nac.repository.NetworkResourcesRepository;
import com.zhmenko.web.nac.exceptions.BadRequestException;
import com.zhmenko.web.nac.mapper.NacRoleListMapper;
import com.zhmenko.web.nac.mapper.NacRoleMapper;
import com.zhmenko.web.nac.model.nacrole.request.modify.NacRoleModifyDto;
import com.zhmenko.web.nac.model.nacrole.request.insert.NacRoleInsertDto;
import com.zhmenko.web.nac.model.nacrole.response.NacRoleDto;
import com.zhmenko.web.nac.services.NacRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NacRoleServiceImpl implements NacRoleService {
    private final NacRoleRepository nacRoleRepository;

    private final NetworkResourcesRepository networkResourcesRepository;

    private final NacRoleListMapper nacRoleListMapper;

    private final NacRoleMapper nacRoleMapper;

    @Override
    public void addRoles(List<NacRoleInsertDto> roles) {
        if (roles.size() == 0) return;

        for (NacRoleInsertDto role : roles) {
            // Проверяем, чтобы ролей с таким же именем в бд не существовало
            if (nacRoleRepository.existsByName(role.getName()))
                throw new BadRequestException("Ошибка валидации! Роль с именем \""
                        + role.getName() + "\" уже существует!");
            // и что нет ресурсов с одинаковыми портами
            if (containsNetworkResourcesDuplicate(role.getNetworkResources()))
                throw new BadRequestException("Ошибка валидации! В запросе роль с именем: "
                        + role.getName() + "имеет ресурсы с одинаковыми портами!");
        }
        // а также отсутствие ролей с одинаковыми именами
        long uniqueRoleNamesCnt = roles.stream()
                .map(NacRoleInsertDto::getName)
                .distinct()
                .count();
        if (uniqueRoleNamesCnt != roles.size())
            throw new BadRequestException("Ошибка валидации! В запросе присутствуют роли с одинаковыми именами!");

        // Проверяем, чтобы существовали все сетевые ресурсы, на которые ссылаются импортируемые роли
        List<Integer> ports = roles.stream()
                .map(NacRoleInsertDto::getNetworkResources)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        for (Integer port : ports) {
            if (!networkResourcesRepository.existsById(port))
                throw new BadRequestException("Ошибка валидации! Сетевой ресурс с портом \"" + port + "\" не существует!");
        }

        nacRoleRepository.saveAll(nacRoleListMapper.nacRoleInsertDtosToNacRoleEntities(roles));
    }

    @Override
    @Transactional
    public void updateRoles(List<NacRoleModifyDto> roles) {
        if (roles.size() == 0) return;

        for (NacRoleModifyDto role : roles) {
            // Проверяем, чтобы роли существовали
            if (!nacRoleRepository.existsById(role.getId()))
                throw new BadRequestException("Ошибка валидации! Роли с id \"" + role.getId() + "\" не существует!");

            // и что нет ресурсов с одинаковыми портами
            if (containsNetworkResourcesDuplicate(role.getNetworkResources()))
                throw new BadRequestException("Ошибка валидации! В запросе роль с именем: "
                        + role.getName() + "имеет ресурсы с одинаковыми портами!");
        }
        // проверяем отсутствие ролей с одинаковыми id
        long uniqueRoleIdsCnt = roles.stream()
                .map(NacRoleModifyDto::getId)
                .distinct()
                .count();
        if (uniqueRoleIdsCnt != roles.size())
            throw new BadRequestException("Ошибка валидации! В запросе присутствуют роли с одинаковыми именами!");

        // Проверяем, чтобы существовали все сетевые ресурсы, на которые ссылаются роли
        List<Integer> ports = roles.stream()
                .map(NacRoleModifyDto::getNetworkResources)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        for (Integer port : ports) {
            if (!networkResourcesRepository.existsById(port))
                throw new BadRequestException("Ошибка валидации! Сетевой ресурс с портом \"" + port + "\" не существует!");
        }
        nacRoleRepository.saveAll(nacRoleListMapper.nacRoleModifyDtosToNacRoleEntities(roles));
    }

    @Override
    @Transactional
    public boolean deleteRoleByName(String roleName) {
        if (nacRoleRepository.existsByName(roleName)) {
            nacRoleRepository.deleteByName(roleName);
            return true;
        }
        return false;
    }

    @Override
    public Optional<NacRoleDto> findByName(String roleName) {
        Optional<NacRoleEntity> nacRoleEntity = nacRoleRepository.findByName(roleName);
        return nacRoleEntity.map(nacRoleMapper::nacRoleEntityToNacRoleDto);
    }

    @Override
    public List<NacRoleDto> findAll() {
        return nacRoleListMapper.nacRoleEntitiesToNacRoleDtos(nacRoleRepository.findAll());
    }

    private boolean containsNetworkResourcesDuplicate(List<Integer> networkResources) {
        if (networkResources != null && networkResources.size() > 1) {
            long uniquePortsCnt = networkResources.stream()
                    .distinct()
                    .count();
            return networkResources.size() != uniquePortsCnt;
        }
        return false;
    }
}
