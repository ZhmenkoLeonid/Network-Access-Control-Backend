package com.zhmenko.data.netflow.models.packet;

import com.zhmenko.data.netflow.models.Protocol;
import lombok.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Data
public class NetflowPacketV5 extends NetflowPacket {
    @Getter(value=AccessLevel.NONE)
    @Setter(value=AccessLevel.NONE)
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");

    public NetflowPacketV5() {
        version = 5;
    }

    public NetflowPacketV5(String srcIpAddress, String dstIpAddress, int srcPort, int dstPort
            , Protocol protocol, Timestamp timestamp, String tcpFlags) {
        this.srcIpAddress = srcIpAddress;
        this.dstIpAddress = dstIpAddress;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.timestamp = timestamp;
        this.protocol = protocol;
        this.tcpFlags = tcpFlags;
        version = 5;
    }

    public NetflowPacketV5(NetflowPacketV5 packet){
        this.srcIpAddress = packet.getSrcIpAddress();
        this.dstIpAddress = packet.getDstIpAddress();
        this.srcPort = packet.getSrcPort();
        this.dstPort = packet.getDstPort();
        this.timestamp = packet.getTimestamp();
        this.protocol = packet.getProtocol();
        this.tcpFlags = packet.getTcpFlags();

        version = 5;
    }
    @Override
    public String toString() {
        return "{" +
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
