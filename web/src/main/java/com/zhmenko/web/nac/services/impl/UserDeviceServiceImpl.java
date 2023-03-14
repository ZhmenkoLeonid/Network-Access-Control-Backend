package com.zhmenko.web.nac.services.impl;

import com.zhmenko.ids.data.nac.entity.UserDeviceEntity;
import com.zhmenko.ids.data.nac.repository.UserDeviceRepository;
import com.zhmenko.ids.models.ids.device.NetflowDevice;
import com.zhmenko.ids.models.ids.device.NetflowDeviceList;
import com.zhmenko.ids.models.ids.exception.UserNotExistException;
import com.zhmenko.router.SSH;
import com.zhmenko.web.ids.services.NetflowUserStatisticService;
import com.zhmenko.web.nac.mapper.nacuser.NacUserListMapper;
import com.zhmenko.web.nac.mapper.nacuser.NacUserMapper;
import com.zhmenko.web.nac.model.user_device.UserDeviceDto;
import com.zhmenko.web.nac.model.user_device.request.modify.UserDeviceModifyDto;
import com.zhmenko.web.nac.services.UserDeviceService;
import com.zhmenko.web.netflow.model.NetflowUserStatisticDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserDeviceServiceImpl implements UserDeviceService {
    private final UserDeviceRepository userDeviceRepository;
    private final NacUserMapper nacUserMapper;

    private final NacUserListMapper nacUserListMapper;

    private final NetflowUserStatisticService netflowUserStatisticService;

    @Qualifier("keenetic")
    private final SSH ssh;
    private final NetflowDeviceList netflowDeviceList;

    public UserDeviceServiceImpl(UserDeviceRepository userDeviceRepository, NacUserMapper nacUserMapper, NacUserListMapper nacUserListMapper, NetflowUserStatisticService netflowUserStatisticService, SSH ssh, NetflowDeviceList netflowDeviceList) {
        this.userDeviceRepository = userDeviceRepository;
        this.nacUserMapper = nacUserMapper;
        this.nacUserListMapper = nacUserListMapper;
        this.netflowUserStatisticService = netflowUserStatisticService;
        this.ssh = ssh;
        this.netflowDeviceList = netflowDeviceList;
    }

    @Override
    public boolean isUserExistByMacAddress(String userMacAddress) {
        return netflowDeviceList.isExistByMacAddress(userMacAddress);
    }

    @Override
    @Transactional
    public boolean deleteUserByMacAddress(String macAddress) {
        return netflowDeviceList.deleteDevice(macAddress);
    }

    @Override
    @Transactional
    public void updateUser(UserDeviceModifyDto nacUserDto) {
        UserDeviceEntity oldUser = userDeviceRepository.findByMacAddress(nacUserDto.getMacAddress())
                .orElseThrow(() -> new UserNotExistException("Ошибка при обновлении: не найдено пользователя с mac адресом " + nacUserDto.getMacAddress()));

        NetflowDevice netflowDevice = netflowDeviceList.getUserByMacAddress(nacUserDto.getMacAddress());
        // Добавление в чс или удаление из него
        if (nacUserDto.isBlocked() && !oldUser.getBlackListInfo().getIsBlocked()) {
            oldUser.getBlackListInfo().setWhenBlocked(OffsetDateTime.now());
            netflowDevice.disableTTLTimer();
            ssh.denyDevicePorts(netflowDevice.getCurrentIpAddress(), netflowDevice.getOpenedPorts());
        } else if (!nacUserDto.isBlocked() && oldUser.getBlackListInfo().getIsBlocked()) {
            oldUser.getBlackListInfo().setWhenUnblocked(OffsetDateTime.now());
            //ssh.permitUserPorts(oldUser.getIpAddress(), oldUser.getSecurityUserEntity().getPorts());
        }
        oldUser.getBlackListInfo().setIsBlocked(nacUserDto.isBlocked());
        oldUser.setHostname(nacUserDto.getHostname());
        // update local record
        netflowDevice.setHostname(oldUser.getHostname());
        netflowDevice.setCurrentIpAddress(oldUser.getIpAddress());
        //netflowDevice.setOpenedPorts(oldUser.getSecurityUserEntity().getPorts());
        netflowDevice.setBlockedState(oldUser.getBlackListInfo().getIsBlocked());

        // update db record
        userDeviceRepository.save(oldUser);
    }

    @Override
    public void updateUsers(List<UserDeviceModifyDto> nacUsers) {
        throw new NotImplementedException();
    }

    @Override
    public Optional<UserDeviceDto> findByMacAddress(String macAddress) {
        Optional<UserDeviceEntity> nacUserEntity = userDeviceRepository.findByMacAddress(macAddress);
        return nacUserEntity.map(entity -> {
            UserDeviceDto userDeviceDto = nacUserMapper.nacUserEntityToNacUserDto(entity);
            userDeviceDto.setDeviceStatistic(
                    netflowUserStatisticService.getUserStatisticByMacAddress(userDeviceDto.getMacAddress())
            );
            return userDeviceDto;
        });
    }

    @Override
    public List<UserDeviceDto> findAllUsers() {
        List<UserDeviceDto> userDeviceDtos = nacUserListMapper.nacUserEntitiesToNacUserDtos(userDeviceRepository.findAll());
        if (userDeviceDtos.size() == 0) return userDeviceDtos;
        // fetch and set nac user netflow statistic
        Map<String, NetflowUserStatisticDto> allUsersStats = netflowUserStatisticService.getAll();
        for (UserDeviceDto userDeviceDto : userDeviceDtos) {
            userDeviceDto.setDeviceStatistic(
                    allUsersStats.getOrDefault(userDeviceDto.getMacAddress(), new NetflowUserStatisticDto())
            );
            // берём время окончания сессии для таймера на фронте
            NetflowDevice localUser = netflowDeviceList.getUserByMacAddress(userDeviceDto.getMacAddress());
            if (localUser != null) {
                userDeviceDto.setEndSessionTimeMillis(localUser.getDeviceSessionInfo().getEndSessionTimeMillis());
            }
        }
        return userDeviceDtos;
    }
}
