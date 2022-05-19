package com.zhmenko.web.services;

import com.zhmenko.ids.model.nac.NacUserDto;

import java.util.List;

public interface NacUserService {
    boolean isUserExist(String userMacAddress);

    void deleteUser(String macAddress);

    void updateUser(NacUserDto nacUserDto);

    NacUserDto findByMacAddress(String macAddress);

    List<NacUserDto> findAllUsers();
}
