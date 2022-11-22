package com.zhmenko.data.netflow.models.user.tasks;

import com.zhmenko.data.netflow.models.user.NetflowUser;
import com.zhmenko.data.netflow.models.user.NetflowUserList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

@AllArgsConstructor
@Slf4j
public class UserSessionExpiredTimerTask extends TimerTask {
    private final NetflowUserList netflowUserList;
    private final NetflowUser netflowUser;

    @Override
    public void run() {
        netflowUserList.getUserSessionValideMap().put(netflowUser.getMacAddress(), false);
        log.info("Время сессии истекло для следующего клиента: " + netflowUser);
    }
}
