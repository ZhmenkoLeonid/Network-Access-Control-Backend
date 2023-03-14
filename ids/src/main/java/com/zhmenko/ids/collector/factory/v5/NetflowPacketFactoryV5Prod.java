package com.zhmenko.ids.collector.factory.v5;


import com.zhmenko.ids.models.ids.Protocol;
import com.zhmenko.ids.models.ids.TcpFlags;
import com.zhmenko.ids.models.ids.device.NetflowDevice;
import com.zhmenko.ids.models.ids.device.NetflowDeviceList;
import com.zhmenko.ids.models.ids.exception.UnsupportedProtocolException;
import com.zhmenko.ids.models.ids.packet.NetflowPacketV5;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
@Profile("prod")
public class NetflowPacketFactoryV5Prod implements NetflowPacketFactoryV5 {
    private final Map<String, Protocol> protocolHashMap;
    private final NetflowDeviceList netflowDeviceList;

    public NetflowPacketFactoryV5Prod(NetflowDeviceList netflowDeviceList) {
        this.netflowDeviceList = netflowDeviceList;

        this.protocolHashMap = new HashMap<>();
        for (Protocol value : Protocol.values()) {
            protocolHashMap.put(value.getValue(), value);
        }
    }

    public void addNetflowRecord(String srcIpAddress, String dstIpAddress, String protocol,
                                 String srcPort, String dstPort, Timestamp timestamp, int tcpFlags) {
        NetflowDevice user = netflowDeviceList.getUserByIpAddress(srcIpAddress);
        if (user == null) return;
        // Если сессия не активна, то не добавляем пакет
        if (!user.getDeviceSessionInfo().isSessionActiveState()) return;
        // Проверяем, поддерживается ли протокол
        if (!protocolHashMap.containsKey(protocol)) {
            throw new UnsupportedProtocolException(protocol);
        }
        // Проверяем, есть ли у юзера доступ к порту, к которому он пытается обратиться
        Set<Integer> userPorts = user.getOpenedPorts();
        if (!userPorts.contains(Integer.parseInt(dstPort))) return;
        // Добавляем пакет в список пользователя
        NetflowPacketV5 packetV5 = new NetflowPacketV5(srcIpAddress,
                dstIpAddress,
                Integer.parseInt(srcPort),
                Integer.parseInt(dstPort),
                protocolHashMap.get(protocol),
                timestamp,
                getTcpFlagsFromInt(tcpFlags));
        user.getProtocolsFlowsList().addFlow(packetV5);
    }

    private static String getTcpFlagsFromInt(int tcpFlags) {
        if (tcpFlags < 0) throw new NumberFormatException("Число должно быть >= 0");
        StringBuilder result = new StringBuilder();
        String binarySet = Integer.toBinaryString(tcpFlags);
        for (int i = 0; i < binarySet.length(); i++) {
            if (binarySet.charAt(i) == '1') {
                result.append(TcpFlags.fromInt(binarySet.length() - 1 - i).name()).append(" ");
            }
        }
        if (binarySet.charAt(binarySet.length() - 1) == '1') {
            result.append(TcpFlags.fromInt(binarySet.length() - 1).name());
        }
        return result.toString();
    }
}
