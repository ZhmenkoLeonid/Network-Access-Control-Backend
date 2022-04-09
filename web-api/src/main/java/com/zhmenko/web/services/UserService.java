package com.zhmenko.web.services;

import java.util.List;

public interface UserService {
    public void save(String userIpAddress);

    public void saveList(List<String> usersIpAddresses);

    public void remove(String userIpAddress);

    public void removeList(List<String> usersIpAddresses);

    public String isExist(String userIpAddress);

    public List<String> findAll();
}
