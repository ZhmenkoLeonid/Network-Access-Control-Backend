package com.zhmenko.web.services.impl;

import com.zhmenko.ids.data.NacUserDao;
import com.zhmenko.ids.model.exception.UserNotExistException;
import com.zhmenko.ids.model.nac.NacUserDto;
import com.zhmenko.ids.model.netflow.user.BlackList;
import com.zhmenko.ids.model.netflow.user.NetflowUser;
import com.zhmenko.ids.model.netflow.user.NetflowUserList;
import com.zhmenko.router.SSH;
import com.zhmenko.web.services.NacUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NacUserServiceImpl implements NacUserService {
    private final NacUserDao nacUserDao;
    private final SSH ssh;
    private final NetflowUserList netflowUserList;
    private final BlackList blackList;

    @Override
    public boolean isUserExist(String userMacAddress) {
        return netflowUserList.isExistByMacAddress(userMacAddress);
    }

    @Override
    public void deleteUser(String macAddress) {
        netflowUserList.deleteUser(macAddress);
    }

    @Override
    public void updateUser(NacUserDto nacUserDto) {
        String ipAddress = nacUserDto.getIpAddress();

        NacUserDto oldUser = nacUserDao.findByMacAddress(nacUserDto.getMacAddress());
        if (oldUser == null)
            throw new UserNotExistException("Ошибка при обновлении: не найдено пользователя с mac адресом " + nacUserDto.getMacAddress());

        // Добавление в чс или удаление из него
        if (nacUserDto.isBlacklisted()) {
            // При добавлении в чс порты не открываем в любом случае
            nacUserDto.setPorts(Collections.emptyList());
            blackList.blockUser(ipAddress);
        } else if (blackList.isBlocked(nacUserDto.getIpAddress())) blackList.unblockUser(ipAddress);

        netflowUserList.updateUser(nacUserDto);

        //TODO пока упрощённо, в идеале - убирать из ACL удалённые и добавлять новые порты
        ssh.denyUserPorts(oldUser.getIpAddress(), oldUser.getPorts());
        ssh.permitUserPorts(nacUserDto.getIpAddress(), nacUserDto.getPorts());
    }

    @Override
    public NacUserDto findByMacAddress(String macAddress) {
        return nacUserDao.findByMacAddress(macAddress);
    }

    @Override
    public List<NacUserDto> findAllUsers() {
        return nacUserDao.findAll();
    }
}
