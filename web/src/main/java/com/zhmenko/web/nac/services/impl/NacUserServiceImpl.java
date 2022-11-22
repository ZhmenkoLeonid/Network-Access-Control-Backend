package com.zhmenko.web.nac.services.impl;

import com.zhmenko.data.nac.models.NacUserEntity;
import com.zhmenko.data.nac.repository.BlackListRepository;
import com.zhmenko.data.nac.repository.NacUserRepository;
import com.zhmenko.data.netflow.models.exception.UserNotExistException;
import com.zhmenko.data.netflow.models.user.NetflowUserList;
import com.zhmenko.router.SSH;
import com.zhmenko.web.nac.services.NacUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NacUserServiceImpl implements NacUserService {
    private final NacUserRepository nacUserRepository;
    @Qualifier("keenetic")
    private final SSH ssh;
    private final NetflowUserList netflowUserList;
    //private final BlackList blackList;

    private final BlackListRepository blackListRepository;

    @Override
    public boolean isUserExist(String userMacAddress) {
        return netflowUserList.isExistByMacAddress(userMacAddress);
    }

    @Override
    public void deleteUser(String macAddress) {
        netflowUserList.deleteUser(macAddress);
    }

    @Override
    public void updateUser(NacUserEntity nacUserEntity) {
        String ipAddress = nacUserEntity.getIpAddress();

        NacUserEntity oldUser = nacUserRepository.findByMacAddress(nacUserEntity.getMacAddress());
        if (oldUser == null)
            throw new UserNotExistException("Ошибка при обновлении: не найдено пользователя с mac адресом " + nacUserEntity.getMacAddress());

        // Добавление в чс или удаление из него
        if (nacUserEntity.getBlackListInfo().getIsBlocked() && !oldUser.getBlackListInfo().getIsBlocked()) {
            // При добавлении в чс порты не открываем в любом случае
            if (nacUserEntity.getUserNacRoleEntities().size() > 0) {
                log.error("Пользователь " + nacUserEntity +
                        "имеет роли и при этом его статус меняется на \"Заблокирован\"! " +
                        "Роли не будут учтены!");
                nacUserEntity.setUserNacRoleEntities(Collections.emptySet());
            }
            //blackList.blockUser(ipAddress);
        }

        netflowUserList.updateUser(nacUserEntity);

        //TODO пока упрощённо, в идеале - убирать из ACL удалённые и добавлять новые порты
        ssh.denyUserPorts(oldUser.getIpAddress(), oldUser.getPorts());
        ssh.permitUserPorts(nacUserEntity.getIpAddress(), nacUserEntity.getPorts());
    }

    @Override
    public NacUserEntity findByMacAddress(String macAddress) {
        return nacUserRepository.findByMacAddress(macAddress);
    }

    @Override
    public List<NacUserEntity> findAllUsers() {
        return nacUserRepository.findAll();
    }
}
