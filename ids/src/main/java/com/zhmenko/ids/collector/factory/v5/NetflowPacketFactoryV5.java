package com.zhmenko.ids.collector.factory.v5;

import java.sql.Timestamp;

public interface NetflowPacketFactoryV5 {
    void addNetflowRecord(String srcIpAddress, String dstIpAddress, String protocol,
                                 String srcPort, String dstPort, Timestamp timestamp, int tcpFlags);
}
