package com.zhmenko.web.nac.services.impl;

import com.zhmenko.data.nac.models.BlackListEntity;
import com.zhmenko.data.nac.models.NacUserEntity;
import com.zhmenko.data.security.repository.SecurityUserRepository;
import com.zhmenko.hostvalidation.PacketValidation;
import com.zhmenko.hostvalidation.host.ValidationPacket;
import com.zhmenko.data.nac.repository.NacUserRepository;
import com.zhmenko.data.netflow.models.user.NetflowUser;
import com.zhmenko.data.netflow.models.user.NetflowUserList;
import com.zhmenko.router.SSH;
import com.zhmenko.web.nac.services.NacHostConnectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NacHostConnectServiceImpl implements NacHostConnectService {
    private final PacketValidation validation;
    @Qualifier("keenetic")
    private final SSH ssh;
    private final NetflowUserList userList;

    private final NacUserRepository nacUserDao;

    private final SecurityUserRepository securityUserRepository;

    @Override
    @Transactional
    public boolean connect(ValidationPacket data, String ipAddress) {
        if (validation.check(data)) {
            String macAddress = data.getWinObj().getMacAddress();
            String hostName = data.getHostname();
            NetflowUser netflowUser = userList.getUserByMacAddress(macAddress);
            List<Integer> openPortList = Collections.emptyList();//List.of(45, 50, 1900);
            NacUserEntity nacUserEntity;

            if (netflowUser == null) {
                log.info("User with mac: " + macAddress + " not exist. Create new");
                nacUserEntity = new NacUserEntity(macAddress,
                        hostName,
                        new BlackListEntity(),
                        securityUserRepository.findByUsername(data.getUsername())
                                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + data.getUsername())),
                        ipAddress,
                        new HashSet<>(),
                        Collections.emptySet());
                userList.addUser(nacUserEntity);
            } else {
                log.info("User with mac: " + macAddress + " exist. Update user state");
                // Закрываем порты предыдущего адреса
                nacUserEntity = nacUserDao.findByMacAddress(macAddress);
                ssh.denyUserPorts(netflowUser.getCurrentIpAddress(), nacUserEntity.getPorts());
                // Ставим новый ip
                netflowUser.setCurrentIpAddress(ipAddress);
                nacUserEntity.setIpAddress(ipAddress);
                // порты
                nacUserEntity.setUserNacRoleEntities(Collections.emptySet());
                // и обновляем юзера в бд
                nacUserDao.save(nacUserEntity);
            }
            ssh.permitUserPorts(ipAddress, openPortList);
            return true;
        }
        log.info("Клиент с ip " + ipAddress + " не прошёл pre-connection проверку");
        return false;
    }

    @Override
    public boolean postConnect(ValidationPacket data, String ipAddress) {
        NetflowUser user = Objects.requireNonNull(userList.getUserByIpAddress(ipAddress));
        if (validation.check(data)) {
            userList.updateUserTTLTimer(user);
            return true;
        }
        log.info("Клиент с ip " + ipAddress + " не прошёл post-connection проверку");
        return false;
    }
}
