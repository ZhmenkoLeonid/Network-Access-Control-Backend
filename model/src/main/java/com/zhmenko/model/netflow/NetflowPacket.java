package com.zhmenko.model.netflow;

import lombok.Data;

import java.sql.Date;
import java.util.UUID;

@Data
public abstract class NetflowPacket {
    protected int version;
    protected String hostname;
    protected String srcIpAddress;
    protected String dstIpAddress;
    protected int srcPort;
    protected int dstPort;
    protected Protocol protocol;
    protected String tcpFlags;
    protected Date timestamp;
}