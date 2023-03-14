package com.zhmenko.ids.models.ids.device.tasks;

import com.zhmenko.ids.models.ids.device.NetflowDevice;
import com.zhmenko.ids.models.ids.device.NetflowDeviceList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

@AllArgsConstructor
@Slf4j
public class RemoveDeviceTimerTask extends TimerTask {
    private final NetflowDeviceList netflowDeviceList;
    private final NetflowDevice netflowDevice;
    @Override
    public void run() {
        log.info("Удаление клиента из-за истечения времени. " + netflowDevice.toString());
        netflowDeviceList.deleteDevice(netflowDevice.getMacAddress());
    }
}
