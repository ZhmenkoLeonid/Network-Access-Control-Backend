package com.zhmenko.data.netflow.models.user;

import lombok.Data;
import lombok.ToString;

@Data
public class NetflowUser {
    private String macAddress;
    private String hostname;
    private String currentIpAddress;
    @ToString.Exclude
    private ProtocolsFlowsList protocolsFlowsList;
    private NetflowUserStatistic netflowUserStatistic;

    public NetflowUser(String macAddress, String currentIpAddress, String hostname, long meanValueIntervalMillis) {
        this.macAddress = macAddress;
        this.hostname = hostname;
        this.currentIpAddress = currentIpAddress;
        this.protocolsFlowsList = new ProtocolsFlowsList();
        this.netflowUserStatistic = new NetflowUserStatistic(meanValueIntervalMillis);
    }

    public void updateUserStatistic(NetflowUserStatistic netflowUserStatistic) {
        netflowUserStatistic.setMeanValueIntervalMillis(this.netflowUserStatistic.getMeanValueIntervalMillis());
        this.netflowUserStatistic = netflowUserStatistic;
    }
}