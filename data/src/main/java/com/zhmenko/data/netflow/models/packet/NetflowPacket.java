package com.zhmenko.data.netflow.models.packet;

import com.zhmenko.data.netflow.models.Protocol;
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