package com.zhmenko.web.services;

import java.util.List;

public interface UserService {
    public void add(String userIpAddress);

    public void addList(List<String> usersIpAddresses);

    public void remove(String userIpAddress);

    public void removeList(List<String> usersIpAddresses);

    public String isExist(String userIpAddress);

    public List<String> findAll();
}
