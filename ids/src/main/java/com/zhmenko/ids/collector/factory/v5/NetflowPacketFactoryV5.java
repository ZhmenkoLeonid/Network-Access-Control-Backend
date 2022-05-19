package com.zhmenko.ids.collector.factory.v5;

import com.zhmenko.ids.model.netflow.user.NetflowUserList;
import com.zhmenko.ids.model.exception.BlockedUserException;
import com.zhmenko.ids.model.exception.UnsupportedProtocolException;
import com.zhmenko.ids.model.netflow.packet.NetflowPacket;
import com.zhmenko.ids.model.netflow.packet.NetflowPacketV5;
import com.zhmenko.ids.model.netflow.Protocol;
import com.zhmenko.ids.model.netflow.TcpFlags;
import com.zhmenko.ids.model.netflow.user.BlackList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

@Component
@Slf4j
public class NetflowPacketFactoryV5 {
    private static Map<String, Protocol> protocolHashMap;
    private BlackList blackList;
    private NetflowUserList netflowUserList;
    private int meanValueIntervalMillis;

    public NetflowPacketFactoryV5(BlackList blackList,
                                  NetflowUserList netflowUserList,
                                  @Value("${netflow.analyze.updateMeanValueTimeMillis}") int meanValueIntervalMillis) {
        this.blackList = blackList;
        this.netflowUserList = netflowUserList;

        this.meanValueIntervalMillis = meanValueIntervalMillis;
        protocolHashMap = new HashMap<>();
        for (Protocol value : Protocol.values()) {
            protocolHashMap.put(value.getValue(), value);
        }
    }

    public NetflowPacket createNetflowClass(String srcIpAddress, String dstIpAddress, String protocol,
                                            String srcPort, String dstPort, Timestamp timestamp, int tcpFlags)
            throws UnsupportedProtocolException, BlockedUserException {
        if (!netflowUserList.isExistByIpAddress(srcIpAddress) || blackList.isBlocked(srcIpAddress)) {
            return null;
        }
        // DEV CODE
/*        if (!netflowUserList.isExistByIpAddress(srcIpAddress)) {
            netflowUserList.addUser(new NacUserDto(UUID.randomUUID().toString(), "tipa hostname", srcIpAddress));
        }*/

        if (!protocolHashMap.containsKey(protocol)) {
            throw new UnsupportedProtocolException(protocol);
        }

        NetflowPacketV5 packetV5 = new NetflowPacketV5(srcIpAddress,
                dstIpAddress,
                Integer.parseInt(srcPort),
                Integer.parseInt(dstPort),
                protocolHashMap.get(protocol),
                timestamp,
                getTcpFlagsFromInt(tcpFlags));
        netflowUserList.getUserByIpAddress(srcIpAddress).getProtocolsFlowsList().addFlow(packetV5);

        return packetV5;
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
