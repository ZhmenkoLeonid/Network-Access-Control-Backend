package com.zhmenko.ids.data;

import com.zhmenko.ids.model.nac.NacUserDto;

import java.util.List;

public interface NacUserDao {
    NacUserDto findByMacAddress(String macAddress);

    List<Integer> findPortsByMacAddress(String macAddress);

    List<String> findAlertsByMacAddress(String macAddress);

    void save(NacUserDto nacUserDto);

    void update(NacUserDto nacUserDto);

    void removeByMacAddress(String macAddress);

    void insertAlertsByMacAddress(String macAddress, List<String> alerts);

    void removeAlertsByMacAddress(String macAddress, List<String> alerts);

    void removeAlertsByMacAddress(String macAddress);

    void insertPortsByMacAddress(String macAddress, List<Integer> ports);

    void removePortsByMacAddress(String macAddress, List<Integer> ports);

    void removePortsByMacAddress(String macAddress);

    boolean isExistByMacAddress(String macAddress);

    List<NacUserDto> findAll();
}
