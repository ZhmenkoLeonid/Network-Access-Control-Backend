package com.zhmenko.ids.models.ids.device.tasks;

import com.zhmenko.ids.models.ids.device.NetflowDevice;
import com.zhmenko.router.SSH;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.TimerTask;

@Slf4j
public class DeviceSessionExpiredTimerTask extends TimerTask {
    private final NetflowDevice netflowDevice;

    private final SSH ssh;

    public DeviceSessionExpiredTimerTask(NetflowDevice netflowDevice,@Qualifier("keenetic") SSH ssh) {
        this.netflowDevice = netflowDevice;
        this.ssh = ssh;
    }

    @Override
    public void run() {
        netflowDevice.getDeviceSessionInfo().setSessionActiveState(false);
        log.info("deny response: " + ssh.denyDevicePorts(netflowDevice.getCurrentIpAddress(), netflowDevice.getOpenedPorts()));
        log.info("Время сессии истекло для следующего клиента: " + netflowDevice);
    }
}
