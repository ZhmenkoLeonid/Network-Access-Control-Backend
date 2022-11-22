package com.zhmenko.web.nac.services;

import com.zhmenko.web.nac.model.nacrole.request.modify.NacRoleModifyDto;
import com.zhmenko.web.nac.model.nacrole.request.insert.NacRoleInsertDto;
import com.zhmenko.web.nac.model.nacrole.response.NacRoleDto;

import java.util.List;
import java.util.Optional;

public interface NacRoleService {

    void addRoles(List<NacRoleInsertDto> roles);

    void updateRoles(List<NacRoleModifyDto> roles);

    boolean deleteRoleByName(String roleName);

    Optional<NacRoleDto> findByName(String roleName);

    List<NacRoleDto> findAll();
}
