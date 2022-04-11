package com.zhmenko.model.netflow;

import com.zhmenko.model.user.User;
import lombok.*;

import java.sql.Date;
import java.text.SimpleDateFormat;

@Data
public class NetflowPacketV5 extends NetflowPacket {
    @Getter(value=AccessLevel.NONE)
    @Setter(value= AccessLevel.NONE)
    private SimpleDateFormat dateFormat;

    public NetflowPacketV5() {
        version = 5;
        dateFormat = new SimpleDateFormat("HH:mm:ss.S");
    }

    public NetflowPacketV5(String srcIpAddress, String dstIpAddress, int srcPort, int dstPort
            , Protocol protocol, Date timestamp, String tcpFlags) {
        this.srcIpAddress = srcIpAddress;
        this.dstIpAddress = dstIpAddress;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.timestamp = timestamp;
        this.protocol = protocol;
        this.tcpFlags = tcpFlags;

        hostname = "";
        version = 5;
        dateFormat = new SimpleDateFormat("HH:mm:ss.S");
    }

    public NetflowPacketV5(NetflowPacketV5 packet){
        this.hostname = packet.getHostname();
        this.srcIpAddress = packet.getSrcIpAddress();
        this.dstIpAddress = packet.getDstIpAddress();
        this.srcPort = packet.getSrcPort();
        this.dstPort = packet.getDstPort();
        this.timestamp = packet.getTimestamp();
        this.protocol = packet.getProtocol();
        this.tcpFlags = packet.getTcpFlags();

        version = 5;
        dateFormat = new SimpleDateFormat("HH:mm:ss.S");
    }
/*
    public static List<NetflowPacket>[] getAllLists(){
        List<NetflowPacket>[] allLists = new LinkedList[Protocol.values().length];
        allLists[0] = NetflowPacketV5TCP.getListOfPackets();
        allLists[1] = NetflowPacketV5UDP.getListOfPackets();
        allLists[2] = NetflowPacketV5ICMP.getListOfPackets();
        allLists[3] = NetflowPacketV5IGMP.getListOfPackets();
        return allLists;
    }
*/
    @Override
    public String toString() {
        return "{" +
                "hostname=" + this.hostname +
                ", version=" + this.version +
                ", srcIpAddress=" + this.srcIpAddress +
                ", dstIpAddress='" + this.dstIpAddress + '\'' +
                ", srcPort=" + srcPort +
                ", dstPort=" + dstPort +
                ", protocol=" + protocol +
                ", tcpFlags=" + tcpFlags +
                ", timestamp=" + dateFormat.format(timestamp) +
                '}';
    }
}
