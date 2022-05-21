package com.zhmenko.ids.traffic_analyze.analyzers;

import com.zhmenko.ids.model.netflow.Protocol;
import com.zhmenko.ids.model.netflow.user.NetflowUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class DestinationPortValuesTrafficAnalyzer implements TrafficAnalyzer{
    private int maxUniqueDestinationPortCount;
    @Override
    public List<String> analyze(NetflowUser netflowUser) {
        Map<Protocol, Long> protUniqueDstPortCntMap = netflowUser.getNetflowUserStatistic().getProtocolUniqueDestinationPortCountMap();
        List<String> alerts = new ArrayList<>();
        if (protUniqueDstPortCntMap == null) return alerts;


        for (Protocol protocol : protUniqueDstPortCntMap.keySet()) {
            Long protCnt = protUniqueDstPortCntMap.get(protocol);
            if (protCnt != null && protCnt > maxUniqueDestinationPortCount) {
                String macAddress = netflowUser.getMacAddress();
                String ipAddress = netflowUser.getCurrentIpAddress();
                String hostname = netflowUser.getHostname();
                String notificationMsg = "DPE. Пользователь " + hostname
                        + " с mac=" + macAddress
                        + " с ip=" + ipAddress
                        + " превышает допустимое количество обращений"
                        + " к уникальным портам по протоколу " + protocol;
                log.info(notificationMsg);
                alerts.add(notificationMsg);
            }
        }
        return alerts;
    }
}
