package com.zhmenko.ids.models.ids.device;

import com.zhmenko.ids.data.nac.entity.UserDeviceEntity;
import com.zhmenko.ids.data.nac.repository.UserDeviceRepository;
import com.zhmenko.ids.data.netflow.clickhouse.jdbc.UserStatisticDao;
import com.zhmenko.ids.models.ids.exception.UserNotExistException;
import com.zhmenko.router.SSH;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NetflowDeviceList {
    private final Map<String, NetflowDevice> usersMap;
    private final UserDeviceRepository userDeviceRepository;
    private final UserStatisticDao userStatisticDao;
    @Qualifier("keenetic")
    private final SSH ssh;
    private final long updateMeanValueTimeMillis;
    @Setter
    private long userSessionTTLMillis;

    public NetflowDeviceList(@Value("${netflow.analyze.updateMeanValueTimeMillis}") long updateMeanValueTimeMillis,
                             @Value("${nac.session-ttl}") long userSessionTTLMillis,
                             SSH ssh,
                             UserDeviceRepository userDeviceRepository,
                             UserStatisticDao userStatisticDao) {
        this.userDeviceRepository = userDeviceRepository;
        this.userStatisticDao = userStatisticDao;
        this.ssh = ssh;

        this.updateMeanValueTimeMillis = updateMeanValueTimeMillis;
        this.userSessionTTLMillis = userSessionTTLMillis;
        this.usersMap = new HashMap<>();
    }

    public void loadUsersFromDbDebug() {
        // при инициализации берём сохранённых в бд юзверей
        List<NetflowDevice> usersDtos = userDeviceRepository.findAll().stream()
                .map(nacUser -> {
                    NetflowDevice netflowDevice = new NetflowDevice(nacUser.getMacAddress(),
                            nacUser.getIpAddress(),
                            nacUser.getHostname(),
                            updateMeanValueTimeMillis);
                    netflowDevice.setOpenedPorts(nacUser.getSecurityUserEntity().getPorts());
                    netflowDevice.setBlockedState(nacUser.getBlackListInfo().getIsBlocked());
                    return netflowDevice;
                })
                .collect(Collectors.toList());

        for (NetflowDevice netflowDevice : usersDtos) {
            NetflowDeviceStatistic userStatistic = userStatisticDao
                    .findUserStatisticByMacAddress(netflowDevice.getMacAddress(),
                            updateMeanValueTimeMillis / 1000);
            if (userStatistic != null) netflowDevice.updateUserStatistic(userStatistic);

            log.info("Add device: " + netflowDevice);
            usersMap.put(netflowDevice.getMacAddress(), netflowDevice);
            // TODO должна быть авторизация,
            //  поэтому в конечной версии надо убрать вызов метода тут
            if (!netflowDevice.getBlockedState()) {
                netflowDevice.addTTLTimer(ssh, userSessionTTLMillis);
                if (netflowDevice.getCurrentIpAddress() != null)
                    ssh.permitDevicePorts(netflowDevice.getCurrentIpAddress(), netflowDevice.getOpenedPorts());
            }
        }
    }

    public void loadUsersFromDb() {
        // при инициализации берём сохранённых в бд юзверей
        List<NetflowDevice> usersDtos = userDeviceRepository.findAll().stream()
                .map(nacUser -> {
                    NetflowDevice netflowDevice = new NetflowDevice(nacUser.getMacAddress(),
                            nacUser.getIpAddress(),
                            nacUser.getHostname(),
                            updateMeanValueTimeMillis);
                    netflowDevice.setOpenedPorts(nacUser.getSecurityUserEntity().getPorts());
                    netflowDevice.setBlockedState(nacUser.getBlackListInfo().getIsBlocked());
                    return netflowDevice;
                })
                .collect(Collectors.toList());

        for (NetflowDevice netflowDevice : usersDtos) {
            NetflowDeviceStatistic userStatistic = userStatisticDao
                    .findUserStatisticByMacAddress(netflowDevice.getMacAddress(),
                            updateMeanValueTimeMillis / 1000);
            if (userStatistic != null) netflowDevice.updateUserStatistic(userStatistic);
            log.info("Add device: " + netflowDevice);
            usersMap.put(netflowDevice.getMacAddress(), netflowDevice);
        }
    }

    /**
     * Сохраняет сущность в локальный список и в бд
     * @param userDeviceEntity
     * @return Сохранённый объект
     */
    public NetflowDevice addDevice(UserDeviceEntity userDeviceEntity) {
        NetflowDevice netflowDevice = new NetflowDevice(userDeviceEntity.getMacAddress(),
                userDeviceEntity.getIpAddress(),
                userDeviceEntity.getHostname(),
                updateMeanValueTimeMillis);
        netflowDevice.setOpenedPorts(userDeviceEntity.getSecurityUserEntity().getPorts());
        netflowDevice.setBlockedState(userDeviceEntity.getBlackListInfo().getIsBlocked());

        String macAddress = userDeviceEntity.getMacAddress();
        if (userDeviceRepository.existsById(macAddress)) {
            log.error("Не удалось добавить устройство с mac адресом: " + macAddress + ", т.к. он уже существует");
            return null;
        }
        // save local
        usersMap.put(macAddress, netflowDevice);
        // save db
        userDeviceRepository.save(userDeviceEntity);
        // execute session timer and add ports to acl
        //netflowDevice.addTTLTimer(ssh, userSessionTTLMillis);
/*        //
        updateLocalUsersPortsByMacAddress(List.of(userDeviceEntity.getMacAddress()),
                userDeviceEntity.getSecurityUserEntity().getPorts());*/
        //ssh.permitUserPorts(netflowDevice.getCurrentIpAddress(), );
        return netflowDevice;
    }

    public boolean deleteDevice(String macAddress) {
        Optional<UserDeviceEntity> nacUserEntityOpt = userDeviceRepository.findByMacAddress(macAddress);
        if (nacUserEntityOpt.isEmpty()) {
            log.info("entity is empty. Not found record in db");
            return false;
        }
        UserDeviceEntity userDeviceEntity = nacUserEntityOpt.get();
        log.info("deleting entity: " + userDeviceEntity);
        NetflowDevice removedElement = usersMap.remove(macAddress);
        userDeviceRepository.deleteById(userDeviceEntity.getMacAddress());
        // Если сессия ещё открыта, то надо удалить и порты в маршрутизаторе
        if (removedElement.getDeviceSessionInfo().isSessionActiveState())
            ssh.denyDevicePorts(userDeviceEntity.getIpAddress(), userDeviceEntity.getSecurityUserEntity().getPorts());
        return true;
    }

    public boolean deleteDevice(UserDeviceEntity userDeviceEntity) {
        log.info("deleting entity: " + userDeviceEntity);
        usersMap.remove(userDeviceEntity.getMacAddress());
        userDeviceRepository.deleteById(userDeviceEntity.getMacAddress());
        if (userDeviceEntity.getIpAddress() != null)
            ssh.denyDevicePorts(userDeviceEntity.getIpAddress(), userDeviceEntity.getSecurityUserEntity().getPorts());
        return true;
    }

    public NetflowDevice removeUserFromLocalNetflowListByMacAddress(String macAddress) {
        return usersMap.remove(macAddress);
    }

    public NetflowDevice getUserByMacAddress(String macAddress) {
        NetflowDevice user = usersMap.get(macAddress);
        if (user == null) {
            log.info("User with mac address \"" + macAddress + "\" not found!");
            return null;
        }
        return user;
    }

    public NetflowDevice getUserByIpAddress(String ipAddress) {
        Objects.requireNonNull(ipAddress);
        Collection<NetflowDevice> users = usersMap.values();
        for (NetflowDevice user : users) {
            if (user.getCurrentIpAddress() != null &&
                    user.getCurrentIpAddress().equals(ipAddress)) return user;
        }
        return null;
    }

    public List<NetflowDevice> getUserList() {
        return new ArrayList<>(usersMap.values());
    }

    public boolean isExistByIpAddress(String ipAddress) {
        Objects.requireNonNull(ipAddress);
        Collection<NetflowDevice> users = usersMap.values();
        for (NetflowDevice user : users) {
            if (user.getCurrentIpAddress().equals(ipAddress)) return true;
        }
        return false;
    }

    public boolean isExistByMacAddress(String macAddress) {
        Objects.requireNonNull(macAddress);
        Collection<NetflowDevice> users = usersMap.values();
        for (NetflowDevice user : users) {
            if (user.getMacAddress().equals(macAddress)) return true;
        }
        return false;
    }

    public void updateLocalUserPortsByMacAddress(String macAddress, Set<Integer> ports) {
        if (!usersMap.containsKey(macAddress)) {
            log.error("Ошибка при обновлении портов юзера. " +
                    "Не найден пользователь с mac: " + macAddress);
            throw new UserNotExistException("Ошибка при обновлении портов юзера. " +
                    "Не найден пользователь с mac: " + macAddress);
        }
        log.info("update local ports for mac: " + macAddress + ";ports=" + ports);
        NetflowDevice netflowDevice = usersMap.get(macAddress);
        log.info("deny response: " + ssh.denyDevicePorts(netflowDevice.getCurrentIpAddress(), netflowDevice.getOpenedPorts()));
        log.info("permit response: " + ssh.permitDevicePorts(netflowDevice.getCurrentIpAddress(), ports));
        netflowDevice.setOpenedPorts(ports);
    }

    // Все mac адреса обязательно должны быть привязаны к одному пользователю
    synchronized public void updateLocalUserDevicesPortsByMacAddress(List<String> macAddresses, Set<Integer> ports) {
        if (macAddresses.size() == 0) return;
        // проверяем чтобы все юзеры существовали в локальном списке
        for (String macAddress : macAddresses) {
            if (!usersMap.containsKey(macAddress)) {
                throw new UserNotExistException("Ошибка при обновлении портов юзера. " +
                        "Не найден пользователь с mac: " + macAddress);
            }
        }

        // По контракту все mac адреса устройств в списке принадлежат одному и тому же пользователю
        // => у всех открыты одни и те же порты, поэтому достаточно получить порты у одного из пользователей
        Set<Integer> oldPorts = usersMap.get(macAddresses.get(0)).getOpenedPorts();

        // достаём множество устройств
        List<NetflowDevice> uniqueDevices = macAddresses.stream()
                .distinct()
                .map(usersMap::get)
                .collect(Collectors.toList());
        // достаём ip адреса, которые будем обновлять
        List<String> ipAddressesForUpdate = uniqueDevices.stream()
                // Обновляем в ACL только те устройства, у которых активна сессия и которые не заблокированы
                .filter(user -> user.getDeviceSessionInfo().isSessionActiveState() && !user.getBlockedState())
                .map(NetflowDevice::getCurrentIpAddress)
                .distinct()
                .collect(Collectors.toList());
        log.info("update local ports for users: " + macAddresses + "; ports=" + ports);

        log.info("router log: \n" + ssh.updateDevicesPorts(ipAddressesForUpdate, oldPorts, ports));
        // Устанавливаем новые локальные порты
        for (NetflowDevice uniqueUser : uniqueDevices) {
            uniqueUser.setOpenedPorts(ports);
        }
    }
}