package com.zhmenko.ids.model.netflow.user;

import com.zhmenko.ids.data.BlackListDao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BlackList {
    private final List<String> blockedMacAddressesList;
    private final BlackListDao blackListDao;

    public BlackList(BlackListDao blackListDao) {
        this.blackListDao = blackListDao;
        blockedMacAddressesList = this.blackListDao.getBlackList();
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
