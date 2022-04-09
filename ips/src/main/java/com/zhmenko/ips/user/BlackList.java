package com.zhmenko.ips.user;

import com.zhmenko.dao.BlackListDao;
import com.zhmenko.dao.NetflowDao;
import com.zhmenko.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("singleton")
public class BlackList {
    private List<String> blockedIpAddressessList;
    private boolean busy;

    BlackListDao blackListDao;
    NetflowDao netflowDao;

    public BlackList(@Autowired BlackListDao blackListDao,
                     @Autowired NetflowDao netflowDao) {
        this.blackListDao = blackListDao;
        this.netflowDao = netflowDao;

        blockedIpAddressessList = blackListDao.getBlackList();
    }

    public boolean blockUser(String ipAddress) {
        waitQueue();
        boolean result = false;
        if (!blockedIpAddressessList.contains(ipAddress)) {
            blockedIpAddressessList.add(ipAddress);
            User.deleteUser(ipAddress);
            blackListDao.insertIpIntoBlackList(ipAddress);
            netflowDao.deleteUserFlowsByIp(ipAddress);
            result = true;
        }
        swapBusy();
        return result;
    }

    public boolean unblockUser(String ipAddress) {
        waitQueue();
        boolean result = blockedIpAddressessList.remove(ipAddress);
        if (result) blackListDao.removeIpFromBlackList(ipAddress);
        swapBusy();
        return result;
    }

    public boolean isBlocked(String ipAddress) {
        return blockedIpAddressessList.contains(ipAddress);
    }

    public List<String> getBlockedIpAddressessList() {
        return blockedIpAddressessList;
    }

    private void waitQueue() {
        try {
            while (busy) {
                Thread.sleep(300);
            }
            swapBusy();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void swapBusy() {
        busy = !busy;
    }
}
