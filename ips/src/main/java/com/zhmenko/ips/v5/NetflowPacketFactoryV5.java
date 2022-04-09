package com.zhmenko.ips.v5;

import com.zhmenko.model.exceptions.BlockedUserException;
import com.zhmenko.model.exceptions.UnsupportedProtocolException;
import com.zhmenko.model.netflow.NetflowPacket;
import com.zhmenko.model.netflow.NetflowPacketV5;
import com.zhmenko.model.netflow.Protocol;
import com.zhmenko.model.netflow.TcpFlags;
import com.zhmenko.model.user.User;
import com.zhmenko.ips.user.BlackList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.*;

@Component
@Scope("singleton")
public class NetflowPacketFactoryV5 {
    private static Map<String, Protocol> protocolHashMap;
    private BlackList blackList;
    private int meanValueIntervalMillis;

    public NetflowPacketFactoryV5(@Autowired BlackList blackList,
                                  @Value("${netflow.analyze.updateMeanValueTimeMillis}") int meanValueIntervalMillis) {
        this.blackList = blackList;
        this.meanValueIntervalMillis = meanValueIntervalMillis;
        protocolHashMap = new HashMap<>();
        for (Protocol value : Protocol.values()) {
            protocolHashMap.put(value.getValue(), value);
        }
    }

    public NetflowPacket createNetflowClass(String srcIpAddress, String dstIpAddress, String protocol,
                                            String srcPort, String dstPort, Date timestamp, int tcpFlags)
            throws UnsupportedProtocolException, BlockedUserException {

        if (!User.isExist(srcIpAddress)) {
            if (!blackList.isBlocked(srcIpAddress)) {
                new User(srcIpAddress, meanValueIntervalMillis);
            } else {
                return null;
            }
        }
        if (!protocolHashMap.containsKey(protocol)) {
            throw new UnsupportedProtocolException(protocol);
        }

        return new NetflowPacketV5(srcIpAddress,
                dstIpAddress,
                Integer.parseInt(srcPort),
                Integer.parseInt(dstPort),
                protocolHashMap.get(protocol),
                timestamp,
                getTcpFlagsFromInt(tcpFlags));
    }

    private static String getTcpFlagsFromInt(int tcpFlags) {
        if (tcpFlags < 0) throw new NumberFormatException("неправильные входные данные, число должно быть не меньше нуля");
        String result = "";
        String binarySet = Integer.toBinaryString(tcpFlags);
        for (int i = 0; i < binarySet.length() - 1; i++) {
            if (binarySet.charAt(i) == '1') {
                result += TcpFlags.fromInt(binarySet.length() - 1 - i).name() + " ";
            }
        }
        if (binarySet.charAt(binarySet.length() - 1) == '1') {
            result += TcpFlags.fromInt(binarySet.length() - 1).name();
        }
        return result;
    }
}
