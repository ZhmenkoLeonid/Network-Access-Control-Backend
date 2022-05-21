package com.zhmenko.ids.model.netflow.user.tasks;

import com.zhmenko.ids.model.netflow.user.NetflowUser;
import com.zhmenko.ids.model.netflow.user.NetflowUserList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

@AllArgsConstructor
@Slf4j
public class RemoveUserTimerTask extends TimerTask {
    private final NetflowUserList netflowUserList;
    private final NetflowUser netflowUser;
    @Override
    public void run() {
        log.info("Удаление клиента из-за истечения времени. " + netflowUser.toString());
        netflowUserList.deleteUser(netflowUser.getMacAddress());
    }
}
