package com.zhmenko.ids.data;

import java.util.List;

public interface BlackListDao {
    List<String> getBlackList();

    void insertUserIntoBlackListByMac(String macAddress);

    void removeUserFromBlackListByMacAddress(String macAddress);

    boolean isBlocked(String macAddress);
}
