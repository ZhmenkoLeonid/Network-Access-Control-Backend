package com.zhmenko.ids.model.netflow.user;

import com.zhmenko.ids.data.BlackListDao;
import com.zhmenko.ids.data.NetflowDao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BlackList {
    private final List<String> blockedMacAddressesList;

    private final BlackListDao blackListDao;
    private final NetflowDao netflowDao;
    private final NetflowUserList netflowUserList;

    public BlackList(BlackListDao blackListDao,
                     NetflowDao netflowDao,
                     NetflowUserList netflowUserList) {
        this.netflowUserList = netflowUserList;
        this.blackListDao = blackListDao;
        this.netflowDao = netflowDao;

        blockedMacAddressesList = blackListDao.getBlackList();
    }

    public boolean blockUser(String macAddress) {
        boolean result = false;
        if (!blockedMacAddressesList.contains(macAddress)) {
            blockedMacAddressesList.add(macAddress);
            blackListDao.insertUserIntoBlackListByMac(macAddress);
            result = true;
        }
        return result;
    }

    public void unblockUser(String macAddress) {
        boolean result = blockedMacAddressesList.remove(macAddress);
        if (result) blackListDao.removeUserFromBlackListByMacAddress(macAddress);
    }

    public boolean isBlocked(String ipAddress) {
        return blockedMacAddressesList.contains(ipAddress);
    }

    public List<String> getBlockedMacAddressesList() {
        return blockedMacAddressesList;
    }
}
