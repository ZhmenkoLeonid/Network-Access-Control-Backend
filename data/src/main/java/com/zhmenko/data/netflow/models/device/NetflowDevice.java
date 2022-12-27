package com.zhmenko.data.netflow.models.device;

import com.zhmenko.router.SSH;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
public class NetflowDevice {
    private String macAddress;
    private String hostname;
    private String currentIpAddress;

    private Boolean blockedState;

    private DeviceSessionInfo deviceSessionInfo;

    private Set<Integer> openedPorts;
    @ToString.Exclude
    private ProtocolsFlowsList protocolsFlowsList;
    private NetflowDeviceStatistic netflowDeviceStatistic;

    public NetflowDevice(String macAddress, String currentIpAddress, String hostname, long meanValueIntervalMillis) {
        this.macAddress = macAddress;
        this.hostname = hostname;
        this.currentIpAddress = currentIpAddress;
        this.protocolsFlowsList = new ProtocolsFlowsList();
        this.netflowDeviceStatistic = new NetflowDeviceStatistic(meanValueIntervalMillis);
        this.blockedState = false;
        this.deviceSessionInfo = new DeviceSessionInfo();
        this.openedPorts = new HashSet<>();
    }

    public void updateUserStatistic(NetflowDeviceStatistic netflowDeviceStatistic) {
        netflowDeviceStatistic.setMeanValueIntervalMillis(this.netflowDeviceStatistic.getMeanValueIntervalMillis());
        this.netflowDeviceStatistic = netflowDeviceStatistic;
    }

    public void addTTLTimer(SSH ssh, long userSessionTTLMillis) {
        deviceSessionInfo.addTTLTimer(this, ssh, userSessionTTLMillis);
    }

    public void updateTTLTimer(SSH ssh, long userSessionTTLMillis) {
        deviceSessionInfo.updateTTLTimer(this, ssh, userSessionTTLMillis);
    }

    public void updateTTLTimer(SSH ssh) {
        deviceSessionInfo.updateTTLTimer(this, ssh);
    }

    public void disableTTLTimer() {
        deviceSessionInfo.disableTTLTimer();
    }
}