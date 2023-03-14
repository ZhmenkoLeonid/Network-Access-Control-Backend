package com.zhmenko.web.nac.services.impl;

import com.zhmenko.ids.data.nac.entity.UserBlockInfoEntity;
import com.zhmenko.ids.data.nac.entity.UserDeviceEntity;
import com.zhmenko.ids.data.nac.repository.UserDeviceRepository;
import com.zhmenko.ids.data.security.repository.SecurityUserRepository;
import com.zhmenko.ids.models.ids.device.NetflowDevice;
import com.zhmenko.ids.models.ids.device.NetflowDeviceList;
import com.zhmenko.ids.models.ids.exception.UserNotExistException;
import com.zhmenko.hostvalidation.PacketValidation;
import com.zhmenko.hostvalidation.host.ValidationPacket;
import com.zhmenko.router.SSH;
import com.zhmenko.web.nac.exceptions.connect.SessionException;
import com.zhmenko.web.nac.exceptions.illegal_state.DeviceBannedException;
import com.zhmenko.web.nac.services.NacHostConnectService;
import com.zhmenko.web.security.services.impl.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@Slf4j
public class NacHostConnectServiceImpl implements NacHostConnectService {
    private final PacketValidation validation;
    @Qualifier("keenetic")
    private final SSH ssh;

    @Value("${nac.session-ttl}")
    private long userSessionTTLMillis;
    private final NetflowDeviceList deviceList;

    private final UserDeviceRepository userDeviceRepository;

    private final SecurityUserRepository securityUserRepository;

    public NacHostConnectServiceImpl(PacketValidation validation, SSH ssh, NetflowDeviceList deviceList, UserDeviceRepository userDeviceRepository, SecurityUserRepository securityUserRepository) {
        this.validation = validation;
        this.ssh = ssh;
        this.deviceList = deviceList;
        this.userDeviceRepository = userDeviceRepository;
        this.securityUserRepository = securityUserRepository;
    }

    @Override
    @Transactional
    public boolean connect(ValidationPacket data) {
        String ipAddress = data.getIpAddress();
        String macAddress = data.getMacAddress();
        String hostName = data.getHostname();
        if (!validation.check(data)) {
            log.info("Клиент с ip " + ipAddress + " не прошёл pre-connection проверку");
            return false;
        }
        log.info("Клиент с ip " + ipAddress + " прошёл pre-connection проверку");
        NetflowDevice userByIpAddress = deviceList.getUserByIpAddress(ipAddress);
        log.info(userByIpAddress ==null ? "null":userByIpAddress.toString());
        if (userByIpAddress != null) {
            if (userByIpAddress.getDeviceSessionInfo().isSessionActiveState()) {
                throw new SessionException("Не удалось совершить подключения, т.к. данный ip-адрес уже имеет активную сессию");
                // Если ip адрес привязан к завершённой сессии, то удаляем адрес у неё
            } else {
                userByIpAddress.setCurrentIpAddress(null);
                UserDeviceEntity userDeviceEntity = userDeviceRepository.findByIpAddress(ipAddress).orElseThrow(RuntimeException::new);
                userDeviceEntity.setIpAddress(null);
            }
        }

        NetflowDevice netflowDevice = deviceList.getUserByMacAddress(macAddress);
        UserDeviceEntity userDeviceEntity;

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (netflowDevice == null) {
            log.info("User with mac: " + macAddress + " not exist. Create new");
            UserBlockInfoEntity userBlockInfoEntity = new UserBlockInfoEntity();
            userBlockInfoEntity.setIsBlocked(false);
            userDeviceEntity = new UserDeviceEntity(macAddress,
                    hostName,
                    userBlockInfoEntity,
                    securityUserRepository.findByUsername(userDetails.getUsername())
                            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + userDetails.getUsername())),
                    ipAddress,
                    new HashSet<>());
            userBlockInfoEntity.setUserDeviceEntity(userDeviceEntity);
            deviceList.addDevice(userDeviceEntity)
                    .addTTLTimer(ssh,userSessionTTLMillis);
        } else {
            log.info("User with mac: " + macAddress + " exist. Update user state");
            if (netflowDevice.getBlockedState())
                throw new DeviceBannedException("Устройство с mac адресом " + macAddress + " заблокировано!");
            // Закрываем порты предыдущего адреса
            userDeviceEntity = userDeviceRepository.findByMacAddress(macAddress)
                    .orElseThrow(() -> new IllegalStateException("Ошибка синхронизации списков"));
            if (netflowDevice.getCurrentIpAddress() != null)
                ssh.denyDevicePorts(netflowDevice.getCurrentIpAddress(), userDeviceEntity.getSecurityUserEntity().getPorts());
            // Ставим новый ip
            netflowDevice.setCurrentIpAddress(ipAddress);
            userDeviceEntity.setIpAddress(ipAddress);
            // и обновляем юзера в бд
            userDeviceRepository.save(userDeviceEntity);
            netflowDevice.addTTLTimer(ssh, userSessionTTLMillis);
        }
        ssh.permitDevicePorts(ipAddress, userDeviceEntity.getSecurityUserEntity().getPorts());
        return true;
    }

    @Override
    public boolean postConnect(ValidationPacket data) {
        String ipAddress = data.getIpAddress();
        String macAddress = data.getMacAddress();

        NetflowDevice user = deviceList.getUserByIpAddress(ipAddress);
        if (user == null)
            throw new UserNotExistException("Устройство с ip-адресом \"" + ipAddress + "\" не найдено!");

        if (!user.getMacAddress().equals(macAddress))
            throw new SessionException("У устройства с mac-адресом " + macAddress
                    + " нет активной сессии с ip адресом " + ipAddress);
        if (user.getBlockedState())
            throw new DeviceBannedException("Устройство с mac адресом " + macAddress + " заблокировано!");

        if (validation.check(data)) {
            log.info("Клиент с ip " + ipAddress + " прошёл post-connection проверку");
            boolean oldSessionActiveState = user.getDeviceSessionInfo().isSessionActiveState();
            user.updateTTLTimer(ssh, userSessionTTLMillis);
            if (!oldSessionActiveState)
                ssh.permitDevicePorts(user.getCurrentIpAddress(), user.getOpenedPorts());
            return true;
        }

        log.info("Клиент с ip " + ipAddress + " не прошёл post-connection проверку");
        return false;
    }
}
