package com.zhmenko.web.security.services;

import com.zhmenko.web.security.model.securityusercontroller.SecurityUserDto;
import com.zhmenko.web.security.model.securityusercontroller.request.modify.SecurityUserModifyDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SecurityUserService {
    List<SecurityUserDto> findAll();
    Optional<SecurityUserDto> findById(UUID id);
    void updateUser(SecurityUserModifyDto securityUserModifyDto);

    boolean deleteUserById(UUID id);
}
