package com.zhmenko.ids.model.netflow.user;

import lombok.Data;
import lombok.ToString;

@Data
public class NetflowUser {
    private String macAddress;
    private String hostname;
    private String currentIpAddress;

    @ToString.Exclude
    private ProtocolsFlowsList protocolsFlowsList;
    @ToString.Exclude
    private NetflowUserStatistic netflowUserStatistic;

    public NetflowUser(String macAddress, String currentIpAddress, String hostname, long meanValueIntervalMillis) {
        this.macAddress = macAddress;
        this.hostname = hostname;
        this.currentIpAddress = currentIpAddress;
        this.protocolsFlowsList = new ProtocolsFlowsList();
        netflowUserStatistic = new NetflowUserStatistic(meanValueIntervalMillis);
    }

    public void updateUserStatistic(NetflowUserStatistic netflowUserStatistic) {
        netflowUserStatistic.setMeanValueIntervalMillis(this.netflowUserStatistic.getMeanValueIntervalMillis());
        this.netflowUserStatistic = netflowUserStatistic;
    }
}