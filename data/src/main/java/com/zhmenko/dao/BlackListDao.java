package com.zhmenko.dao;

import java.util.List;

public interface BlackListDao {
    public List<String> getBlackList();

    public void insertIpIntoBlackList(String ipAddress);

    public void removeIpFromBlackList(String ipAddress);
}
