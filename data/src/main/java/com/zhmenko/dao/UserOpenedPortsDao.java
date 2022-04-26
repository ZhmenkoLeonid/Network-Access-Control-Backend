package com.zhmenko.dao;

import java.util.List;

public interface UserOpenedPortsDao {

    public void save(String ipAddress, int port);

    public void saveList(String ipAddress, List<Integer> ports);

    public void remove(String ipAddress, int port);

    public void removeAll(String ipAddress);

    public List<Integer> findUserPorts(String ipAddress);
}
