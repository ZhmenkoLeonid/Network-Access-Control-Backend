package com.zhmenko.web.nac.services;

import com.zhmenko.web.nac.model.user_device.UserDeviceDto;
import com.zhmenko.web.nac.model.user_device.request.modify.UserDeviceModifyDto;

import java.util.List;
import java.util.Optional;

public interface UserDeviceService {
    boolean isUserExistByMacAddress(String userMacAddress);

    boolean deleteUserByMacAddress(String macAddress);

    void updateUser(UserDeviceModifyDto nacUserDto);

    void updateUsers(List<UserDeviceModifyDto> nacUsers);

    Optional<UserDeviceDto> findByMacAddress(String macAddress);

    List<UserDeviceDto> findAllUsers();
}
