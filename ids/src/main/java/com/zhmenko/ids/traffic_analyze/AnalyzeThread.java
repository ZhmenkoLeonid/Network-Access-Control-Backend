package com.zhmenko.ids.traffic_analyze;

import com.zhmenko.data.nac.models.UserBlockInfoEntity;
import com.zhmenko.data.nac.models.UserDeviceAlertEntity;
import com.zhmenko.data.nac.models.UserDeviceEntity;
import com.zhmenko.data.nac.repository.UserBlockInfoRepository;
import com.zhmenko.data.nac.repository.UserDeviceRepository;
import com.zhmenko.data.netflow.NetflowDao;
import com.zhmenko.data.netflow.UserStatisticDao;
import com.zhmenko.data.netflow.models.device.NetflowDevice;
import com.zhmenko.data.netflow.models.device.NetflowDeviceList;
import com.zhmenko.data.netflow.models.exception.UserNotExistException;
import com.zhmenko.data.netflow.models.packet.NetflowPacket;
import com.zhmenko.ids.traffic_analyze.analyzers.TrafficAnalyzer;
import com.zhmenko.router.SSH;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@Profile({"dev", "prod"})
public class AnalyzeThread extends Thread {

    /* берём 3000 пакетов в час для каждого протокола как средние */
    //private final double defaultMeanValueMillisMultiplier = 0.0008333;

    //private long defaultMeanValue;
    private long timerExecuteTimeMillis;

    private final SSH ssh;
    private final NetflowDao netflowDao;
    private final UserStatisticDao userStatisticDao;
    private final UserDeviceRepository userDeviceRepository;
    private final UserBlockInfoRepository userBlockInfoRepository;
    private final NetflowDeviceList netflowDeviceList;

    private final AnalyzeProperties properties;

    private final List<TrafficAnalyzer> trafficAnalyzers;

    public AnalyzeThread(@Qualifier("keenetic") SSH ssh,
                         NetflowDao netflowDao,
                         UserStatisticDao userStatisticDao,
                         UserDeviceRepository userDeviceRepository,
                         UserBlockInfoRepository userBlockInfoRepository,
                         NetflowDeviceList netflowDeviceList,
                         AnalyzeProperties properties,
                         List<TrafficAnalyzer> trafficAnalyzers) {
        this.ssh = ssh;
        this.netflowDao = netflowDao;
        this.userStatisticDao = userStatisticDao;
        this.userDeviceRepository = userDeviceRepository;
        this.userBlockInfoRepository = userBlockInfoRepository;
        this.netflowDeviceList = netflowDeviceList;
        this.properties = properties;
        this.trafficAnalyzers = trafficAnalyzers;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                log.info("analyze invoke");
                for (NetflowDevice netflowDevice : netflowDeviceList.getUserList()) {
                    // Получаем последнюю статистику, если она обновлялась
                    if (!netflowDevice.getProtocolsFlowsList().isEmpty()) {
                        String macAddress = netflowDevice.getMacAddress();
                        String ipAddress = netflowDevice.getCurrentIpAddress();
                        List<NetflowPacket> allFlows = netflowDevice.getProtocolsFlowsList().getAllFlows();
                        netflowDao.saveList(allFlows, macAddress);
                        log.info("save " + allFlows.size() + " flows for user: " + netflowDevice);
                        //netflowService.saveProtocolListMap(user.getProtocolsFlowsList().getProtocolListHashMap(), user.getMacAddress());
                        netflowDevice.getProtocolsFlowsList().clear();

                        netflowDevice.updateUserStatistic(userStatisticDao
                                .findUserStatisticByMacAddress(macAddress, netflowDevice.getNetflowDeviceStatistic().getMeanValueIntervalMillis() / 1000));
                    }
                    // проверяем по реализованным требованиям
                    List<String> alerts = new ArrayList<>();
                    for (TrafficAnalyzer trafficAnalyzer : trafficAnalyzers) {
                        alerts.addAll(trafficAnalyzer.analyze(netflowDevice));
                    }
                    // Если нашлись нарушения, запоминаем их в базу данных
                    if (alerts.size() > 0) {
                        UserDeviceEntity user = userDeviceRepository.findByMacAddress(netflowDevice.getMacAddress())
                                .orElseThrow(UserNotExistException::new);
                        user.getAlerts().addAll(alerts.stream()
                                .map(alert -> UserDeviceAlertEntity.builder()
                                        .alertMessage(alert)
                                        .userDeviceEntity(user).build())
                                .collect(Collectors.toList()));
                        userDeviceRepository.save(user);
                        //nacUserDao.insertAlertsByMacAddress(netflowUser.getMacAddress(), alerts);
                        // блокировка для теста
                        //blockUserByMac(netflowDevice.getMacAddress());
                    }
                }
                timerExecuteTimeMillis = properties.getAnalyzeFrequencyMillis() + System.currentTimeMillis();
                properties.setAnalyzeExecuteTime(timerExecuteTimeMillis);
                Thread.sleep(properties.getAnalyzeFrequencyMillis());
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void blockUserByMac(String macAddress) {
        // Добавляем в чс
        UserBlockInfoEntity userBlockInfoEntity = userBlockInfoRepository.findById(macAddress).orElseThrow(() -> new UserNotExistException("Не удалось заблокировать пользователя! " +
                "Пользователь с mac-адресом \"" + macAddress + "\" не существует!"));
        if (userBlockInfoEntity.getIsBlocked()) throw new IllegalStateException(
                "Пользователь с mac-адресом \"" + macAddress + "\" уже заблокирован!");
        //blackList.blockUser(macAddress);
        // Закрываем открытые порты для всех ip адресов, связанных с mac-адресом юзера
        //ssh.denyUserPorts(nacUserEntity.getIpAddress(), nacUserEntity.getPorts());
        netflowDeviceList.removeUserFromLocalNetflowListByMacAddress(macAddress);
        userBlockInfoEntity.setIsBlocked(true);
        userBlockInfoEntity.setWhenBlocked(OffsetDateTime.now());

        userBlockInfoRepository.save(userBlockInfoEntity);

        UserDeviceEntity byMacAddress = userDeviceRepository.findByMacAddress(macAddress)
                .orElseThrow(UserNotExistException::new);
        byMacAddress.setIpAddress(null);
        userDeviceRepository.save(byMacAddress);
    }
}