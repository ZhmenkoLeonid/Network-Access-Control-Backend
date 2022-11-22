/*
package com.zhmenko.data.netflow.models.user;

import com.zhmenko.data.nac.BlackListDao;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class BlackList {
    private final List<String> blockedMacAddressesList = new ArrayList<>();
    private final BlackListDao blackListDao;

    public BlackList(BlackListDao blackListDao) {
        this.blackListDao = blackListDao;
        //TODO vernut'
        //blockedMacAddressesList = this.blackListDao.getBlackList();
    }

    public boolean blockUser(String macAddress) {
        boolean result = false;
        if (!blockedMacAddressesList.contains(macAddress)) {
            blockedMacAddressesList.add(macAddress);
            result = true;
        }
        return result;
    }

    public boolean unblockUser(String macAddress) {
        return blockedMacAddressesList.remove(macAddress);
    }

    public boolean isBlocked(String ipAddress) {
        return blockedMacAddressesList.contains(ipAddress);
    }

    public List<String> getBlockedMacAddressesList() {
        return blockedMacAddressesList;
    }
}
*/
