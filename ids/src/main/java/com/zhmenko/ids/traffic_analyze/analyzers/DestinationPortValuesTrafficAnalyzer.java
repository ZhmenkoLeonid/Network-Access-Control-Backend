package com.zhmenko.ids.traffic_analyze.analyzers;

import com.zhmenko.data.netflow.models.Protocol;
import com.zhmenko.data.netflow.models.user.NetflowUser;
import com.zhmenko.ids.traffic_analyze.AnalyzeProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Component
public class DestinationPortValuesTrafficAnalyzer implements TrafficAnalyzer {
    private AnalyzeProperties properties;
    @Override
    public List<String> analyze(NetflowUser netflowUser) {
        Map<Protocol, Long> protUniqueDstPortCntMap = netflowUser.getNetflowUserStatistic().getProtocolUniqueDestinationPortCountMap();
        List<String> alerts = new ArrayList<>();
        if (protUniqueDstPortCntMap == null) return alerts;


        for (Protocol protocol : protUniqueDstPortCntMap.keySet()) {
            Long protCnt = protUniqueDstPortCntMap.get(protocol);
            if (protCnt != null && protCnt > properties.getMaxUniqueDestinationPortCount()) {
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
