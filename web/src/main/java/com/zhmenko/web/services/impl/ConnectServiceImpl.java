package com.zhmenko.web.services.impl;

import com.zhmenko.ids.data.NacUserDao;
import com.zhmenko.ids.model.nac.NacUserDto;
import com.zhmenko.hostvalidation.PacketValidation;
import com.zhmenko.hostvalidation.host.ValidationPacket;
import com.zhmenko.ids.model.netflow.user.NetflowUser;
import com.zhmenko.ids.model.netflow.user.NetflowUserList;
import com.zhmenko.router.SSH;
import com.zhmenko.web.services.ConnectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectServiceImpl implements ConnectService {
    private final PacketValidation validation;
    private final SSH ssh;
    private final NetflowUserList userList;

    private final NacUserDao nacUserDao;

    @Override
    public boolean connect(ValidationPacket data, String ipAddress) {
        if (validation.check(data)) {
            String macAddress = data.getWinObj().getMacAddress();
            String hostName = data.getHostname();
            NetflowUser netflowUser = userList.getUserByMacAddress(macAddress);
            List<Integer> openPortList = List.of(45, 50, 1900);
            NacUserDto nacUserDto = new NacUserDto(macAddress,hostName,false,ipAddress,openPortList, Collections.emptyList());

            if (netflowUser == null) {
                log.info("User with mac: " + macAddress + " not exist. Create new");
                userList.addUser(nacUserDto);
            } else {
                log.info("User with mac: " + macAddress + " exist. Update user state");
                // Закрываем порты предыдущего адреса
                List<Integer> oldIpOpenedPorts = nacUserDao.findPortsByMacAddress(macAddress);
                ssh.denyUserPorts(netflowUser.getCurrentIpAddress(),oldIpOpenedPorts);
                // Ставим новый ip
                netflowUser.setCurrentIpAddress(ipAddress);
                // и обновляем юзера в бд
                nacUserDao.update(nacUserDto);
            }
            ssh.permitUserPorts(ipAddress, openPortList);
            return true;
        }
        log.info("Клиент с ip " + ipAddress + " не прошёл проверку");
        return false;
    }
}
