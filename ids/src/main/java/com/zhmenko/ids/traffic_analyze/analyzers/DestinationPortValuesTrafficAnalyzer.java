package com.zhmenko.ids.traffic_analyze.analyzers;

import com.zhmenko.data.netflow.models.Protocol;
import com.zhmenko.data.netflow.models.device.NetflowDevice;
import com.zhmenko.ids.traffic_analyze.AnalyzeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"dev","prod"})
public class DestinationPortValuesTrafficAnalyzer implements TrafficAnalyzer {
    private final AnalyzeProperties properties;
    @Override
    public List<String> analyze(NetflowDevice netflowDevice) {
        Map<Protocol, Long> protUniqueDstPortCntMap = netflowDevice.getNetflowDeviceStatistic().getProtocolUniqueDestinationPortCountMap();
        List<String> alerts = new ArrayList<>();
        if (protUniqueDstPortCntMap == null) return alerts;

        for (Protocol protocol : protUniqueDstPortCntMap.keySet()) {
            Long protCnt = protUniqueDstPortCntMap.get(protocol);
            if (protCnt != null && protCnt > properties.getMaxUniqueDestinationPortCount()) {
                String macAddress = netflowDevice.getMacAddress();
                String ipAddress = netflowDevice.getCurrentIpAddress();
                String hostname = netflowDevice.getHostname();
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
