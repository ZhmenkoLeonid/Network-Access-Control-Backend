package com.zhmenko.web.services.impl;

import com.zhmenko.dao.UserDao;
import com.zhmenko.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao dao;

    @Override
    public void save(String userIpAddress) {
        dao.save(userIpAddress);
    }

    @Override
    public void saveList(List<String> usersIpAddresses) {
        dao.saveList(usersIpAddresses);
    }

    @Override
    public void remove(String userIpAddress) {
        dao.remove(userIpAddress);
    }

    @Override
    public void removeList(List<String> usersIpAddresses) {
        dao.removeList(usersIpAddresses);
    }

    @Override
    public String isExist(String userIpAddress) {
        String answer = "Пользователь с ip " + userIpAddress;
        if (!dao.isExist(userIpAddress)) answer += " не";
        answer += " существует!";

        return answer;
    }

    @Override
    public List<String> findAll() {
        return dao.findAll();
    }
}
