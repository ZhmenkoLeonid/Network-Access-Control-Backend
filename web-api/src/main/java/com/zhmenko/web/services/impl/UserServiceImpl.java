package com.zhmenko.web.services.impl;

import com.zhmenko.dao.UserDao;
import com.zhmenko.dao.list.user.UserList;
import com.zhmenko.model.user.User;
import com.zhmenko.web.services.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final UserList userList;

    @Value("${netflow.analyze.updateMeanValueTimeMillis}")
    private long updateMeanValueTimeMillis;

    @Override
    public void addUser(String userIpAddress) {
        userList.addUser(userIpAddress);
/*        userDao.save(userIpAddress);
        //TODO це х**ня, надо по-другому создавать (addUser??)
        new User(userIpAddress, updateMeanValueTimeMillis);*/
    }

    @Override
    public void addUserList(List<String> usersIpAddresses) {
        userList.addUsers(usersIpAddresses);
        //userDao.saveList(usersIpAddresses);
    }

    @Override
    public void removeUser(String userIpAddress) {
        userList.deleteUser(userIpAddress);
        //userDao.remove(userIpAddress);
    }

    @Override
    public void removeUserList(List<String> usersIpAddresses) {
        throw new AssertionError();
        //userList.d
        //userDao.removeList(usersIpAddresses);
    }

    @Override
    public String isUserExist(String userIpAddress) {
        String answer = "Пользователь с ip " + userIpAddress;
        if (!userDao.isExist(userIpAddress)) answer += " не";
        answer += " существует!";

        return answer;
    }

    @Override
    public List<String> findAllUsers() {
        return userDao.findAll();
    }
}
