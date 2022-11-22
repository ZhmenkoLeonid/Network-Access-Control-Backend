package com.zhmenko.web.nac.services;

import com.zhmenko.data.nac.models.NacUserEntity;

import java.util.List;

public interface NacUserService {
    boolean isUserExist(String userMacAddress);

    void deleteUser(String macAddress);

    void updateUser(NacUserEntity nacUserEntity);

    NacUserEntity findByMacAddress(String macAddress);

    List<NacUserEntity> findAllUsers();
}
