package com.zhmenko.ids.traffic_analyze;

import com.zhmenko.ids.data.NacUserDao;
import com.zhmenko.ids.data.NetflowDao;
import com.zhmenko.ids.data.UserStatisticDao;
import com.zhmenko.ids.model.nac.NacUserDto;
import com.zhmenko.ids.model.netflow.user.NetflowUserList;
import com.zhmenko.ids.model.netflow.user.NetflowUser;
import com.zhmenko.ids.model.netflow.user.BlackList;
import com.zhmenko.ids.traffic_analyze.analyzers.DestinationPortValuesTrafficAnalyzer;
import com.zhmenko.ids.traffic_analyze.analyzers.MeanPacketValueTrafficAnalyzer;
import com.zhmenko.ids.traffic_analyze.analyzers.TrafficAnalyzer;
import com.zhmenko.router.SSH;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class AnalyzeThread extends Thread {

    /* берём 3000 пакетов в час для каждого протокола как средние */
    //private final double defaultMeanValueMillisMultiplier = 0.0008333;

    //private long defaultMeanValue;
    private static long timerExecuteTimeMillis;
    private SSH ssh;
    private NetflowDao netflowDao;
    private UserStatisticDao userStatisticDao;
    private NacUserDao nacUserDao;
    private BlackList blackList;
    private NetflowUserList netflowUserList;

    private AnalyzeProperties properties;

    private List<TrafficAnalyzer> trafficAnalyzers;

    public AnalyzeThread(SSH ssh,
                         NetflowDao netflowDao,
                         UserStatisticDao userStatisticDao,
                         NacUserDao nacUserDao,
                         BlackList blackList,
                         NetflowUserList netflowUserList,
                         AnalyzeProperties properties,
                         List<TrafficAnalyzer> trafficAnalyzers) {
        this.ssh = ssh;
        this.netflowDao = netflowDao;
        this.userStatisticDao = userStatisticDao;
        this.nacUserDao = nacUserDao;
        this.blackList = blackList;
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
                        log.info("save flows for user: " + netflowUser.toString());

                        netflowUser.updateUserStatistic(userStatisticDao
                                .findUserStatisticByMacAddress(macAddress, netflowUser.getNetflowUserStatistic().getMeanValueIntervalMillis() / 1000));
                    }
                    // проверяем по реализованным требованиям
                    List<String> alerts = new ArrayList<>();
                    for (TrafficAnalyzer trafficAnalyzer : trafficAnalyzers) {
                        alerts.addAll(trafficAnalyzer.analyze(netflowUser));
                    }
                    // Если нашлись нарушения, запоминаем их в базу данных
                    if (alerts.size() > 0) nacUserDao.insertAlertsByMacAddress(netflowUser.getMacAddress(), alerts);
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
        blackList.blockUser(macAddress);
        try {
            NacUserDto nacUserDto = nacUserDao.findByMacAddress(macAddress);
            // Закрываем открытые порты для всех ip адресов, связанных с mac-адресом юзера
            ssh.denyUserPorts(nacUserDto.getIpAddress(), nacUserDto.getPorts());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getTimeLeftBfrUpdateMillis() {
        return timerExecuteTimeMillis - System.currentTimeMillis();
    }
}