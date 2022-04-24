package com.zhmenko.web.services;

import java.util.List;

public interface UserService {
    public void addUser(String userIpAddress);

    public void addUserList(List<String> usersIpAddresses);

    public void removeUser(String userIpAddress);

    public void removeUserList(List<String> usersIpAddresses);

    public String isUserExist(String userIpAddress);

    public List<String> findAllUsers();
}
