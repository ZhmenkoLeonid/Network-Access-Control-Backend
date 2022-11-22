package com.zhmenko.ids.traffic_analyze;

import com.zhmenko.data.nac.NetflowDao;
import com.zhmenko.data.nac.UserStatisticDao;
import com.zhmenko.data.nac.models.BlackListEntity;
import com.zhmenko.data.nac.models.NacUserAlertEntity;
import com.zhmenko.data.nac.models.NacUserEntity;
import com.zhmenko.data.nac.repository.BlackListRepository;
import com.zhmenko.data.nac.repository.NacUserRepository;
import com.zhmenko.data.netflow.models.exception.UserNotExistException;
import com.zhmenko.data.netflow.models.user.NetflowUser;
import com.zhmenko.data.netflow.models.user.NetflowUserList;
import com.zhmenko.ids.traffic_analyze.analyzers.TrafficAnalyzer;
import com.zhmenko.router.SSH;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AnalyzeThread extends Thread {

    /* берём 3000 пакетов в час для каждого протокола как средние */
    //private final double defaultMeanValueMillisMultiplier = 0.0008333;

    //private long defaultMeanValue;
    private static long timerExecuteTimeMillis;
    @Qualifier("keenetic")
    private SSH ssh;
    private NetflowDao netflowDao;
    private UserStatisticDao userStatisticDao;
    private NacUserRepository nacUserRepository;
    //private BlackList blackList;
    private BlackListRepository blackListRepository;
    private NetflowUserList netflowUserList;

    private AnalyzeProperties properties;

    private List<TrafficAnalyzer> trafficAnalyzers;

    public AnalyzeThread(SSH ssh,
                         NetflowDao netflowDao,
                         UserStatisticDao userStatisticDao,
                         NacUserRepository nacUserRepository,
                         //BlackList blackList,
                         BlackListRepository blackListRepository,
                         NetflowUserList netflowUserList,
                         AnalyzeProperties properties,
                         List<TrafficAnalyzer> trafficAnalyzers) {
        this.ssh = ssh;
        this.netflowDao = netflowDao;
        this.userStatisticDao = userStatisticDao;
        this.nacUserRepository = nacUserRepository;
        //this.blackList = blackList;
        this.blackListRepository = blackListRepository;
        this.netflowUserList = netflowUserList;
        this.properties = properties;
        this.trafficAnalyzers = trafficAnalyzers;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                log.info("analyze invoke");
                for (NetflowUser netflowUser : netflowUserList.getUserList()) {
                    // Получаем последнюю статистику, если она обновлялась
                    if (!netflowUser.getProtocolsFlowsList().isEmpty()) {
                        String macAddress = netflowUser.getMacAddress();
                        String ipAddress = netflowUser.getCurrentIpAddress();
                        netflowDao.saveList(netflowUser.getProtocolsFlowsList().getAllFlows(), macAddress);
                        //netflowService.saveProtocolListMap(user.getProtocolsFlowsList().getProtocolListHashMap(), user.getMacAddress());
                        netflowUser.getProtocolsFlowsList().clear();
                        log.info("save flows for user: " + netflowUser);

                        netflowUser.updateUserStatistic(userStatisticDao
                                .findUserStatisticByMacAddress(macAddress, netflowUser.getNetflowUserStatistic().getMeanValueIntervalMillis() / 1000));
                    }
                    // проверяем по реализованным требованиям
                    List<String> alerts = new ArrayList<>();
                    for (TrafficAnalyzer trafficAnalyzer : trafficAnalyzers) {
                        alerts.addAll(trafficAnalyzer.analyze(netflowUser));
                    }
                    // Если нашлись нарушения, запоминаем их в базу данных
                    if (alerts.size() > 0) {
                        NacUserEntity user = nacUserRepository.findByMacAddress(netflowUser.getMacAddress());
                        user.getAlerts().addAll(alerts.stream()
                                .map(alert -> NacUserAlertEntity.builder()
                                        .alertMessage(alert)
                                        .nacUserEntity(user).build())
                                .collect(Collectors.toList()));
                        nacUserRepository.save(user);
                        //nacUserDao.insertAlertsByMacAddress(netflowUser.getMacAddress(), alerts);
                        // блокировка для теста
                        blockUserByMac(netflowUser.getMacAddress());
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
        BlackListEntity blackListEntity = blackListRepository.findById(macAddress).orElseThrow(() ->  new UserNotExistException("Не удалось заблокировать пользователя! " +
                "Пользователь с mac-адресом \"" + macAddress + "\" не существует!"));
        if (blackListEntity.getIsBlocked()) throw new IllegalStateException(
                "Пользователь с mac-адресом \"" + macAddress + "\" уже заблокирован!");
        //blackList.blockUser(macAddress);
        // Закрываем открытые порты для всех ip адресов, связанных с mac-адресом юзера
        //ssh.denyUserPorts(nacUserEntity.getIpAddress(), nacUserEntity.getPorts());
        netflowUserList.removeUserFromLocalNetflowListByMacAddress(macAddress);
        blackListEntity.setIsBlocked(true);
        blackListEntity.setWhenBlocked(OffsetDateTime.now());

        blackListRepository.save(blackListEntity);

        NacUserEntity byMacAddress = nacUserRepository.findByMacAddress(macAddress);
        byMacAddress.setIpAddress(null);
        nacUserRepository.save(byMacAddress);
    }

    public static long getTimeLeftBfrUpdateMillis() {
        return timerExecuteTimeMillis - System.currentTimeMillis();
    }
}