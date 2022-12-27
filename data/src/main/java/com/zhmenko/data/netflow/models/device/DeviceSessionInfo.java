package com.zhmenko.data.netflow.models.device;

import com.zhmenko.data.netflow.models.device.tasks.DeviceSessionExpiredTimerTask;
import com.zhmenko.router.SSH;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;

@Data
@Slf4j
public class DeviceSessionInfo {
    private boolean sessionActiveState;

    private Timer sessionTimer;

    private Long ttlTimerMillis;

    private Long endSessionTimeMillis;

    public void addTTLTimer(NetflowDevice device, SSH ssh, long userSessionTTLMillis) {
        sessionTimer = new Timer(true);
        sessionTimer.schedule(new DeviceSessionExpiredTimerTask(device, ssh), userSessionTTLMillis);
        endSessionTimeMillis = System.currentTimeMillis() + userSessionTTLMillis;

        ttlTimerMillis = userSessionTTLMillis;
        sessionActiveState = true;
    }

    public void updateTTLTimer(NetflowDevice device, SSH ssh, long userSessionTTLMillis) {
        if (sessionTimer == null) {
            addTTLTimer(device, ssh, userSessionTTLMillis);
            return;
        }
        sessionTimer.cancel();
        sessionTimer.purge();
        sessionTimer = new Timer(true);
        sessionTimer.schedule(new DeviceSessionExpiredTimerTask(device, ssh), userSessionTTLMillis);
        endSessionTimeMillis = System.currentTimeMillis() + userSessionTTLMillis;

        ttlTimerMillis = userSessionTTLMillis;
        sessionActiveState = true;
    }
    public void updateTTLTimer(NetflowDevice device, SSH ssh) {
        if (sessionTimer == null)
            throw new IllegalStateException("Ошибка! Таймер сессии не установлен у устройства " + this);
        sessionTimer.cancel();
        sessionTimer.purge();
        sessionTimer = new Timer(true);
        sessionTimer.schedule(new DeviceSessionExpiredTimerTask(device, ssh), ttlTimerMillis);
        endSessionTimeMillis = System.currentTimeMillis() + ttlTimerMillis;

        sessionActiveState = true;
    }
    public void disableTTLTimer() {
        if (sessionTimer == null) return;
        sessionTimer.cancel();
        sessionTimer.purge();
        endSessionTimeMillis = 0L;
        sessionActiveState = false;
    }
}
