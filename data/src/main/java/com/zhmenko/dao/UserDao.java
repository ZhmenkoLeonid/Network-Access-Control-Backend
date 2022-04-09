package com.zhmenko.dao;

import java.util.List;

public interface UserDao {
    public boolean isExist(String ipAddress);

    public void save(String ipAddress);

    public void saveList(List<String> ipAddresses);

    public void remove(String ipAddress);

    public void removeList(List<String> ipAddresses);

    public List<String> findAll();
}
