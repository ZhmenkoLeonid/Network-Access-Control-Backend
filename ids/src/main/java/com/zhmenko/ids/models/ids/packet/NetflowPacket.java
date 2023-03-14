package com.zhmenko.ids.models.ids.packet;

import com.zhmenko.ids.models.ids.Protocol;
import lombok.Data;

import java.sql.Timestamp;

@Data
public abstract class NetflowPacket {
    protected int version;
    protected String srcIpAddress;
    protected String dstIpAddress;
    protected int srcPort;
    protected int dstPort;
    protected Protocol protocol;
    protected String tcpFlags;
    protected Timestamp timestamp;
}