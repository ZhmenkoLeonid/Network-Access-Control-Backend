package com.zhmenko.dao.list.user;

import com.zhmenko.dao.NetflowDao;
import com.zhmenko.dao.UserDao;
import com.zhmenko.dao.UserOpenedPortsDao;
import com.zhmenko.model.user.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserList {
    private Map<String, User> usersMap;
    private UserDao userDao;
    private NetflowDao netflowDao;
    private UserOpenedPortsDao userOpenedPortsDao;
    private long updateMeanValueTimeMillis;

    public UserList(UserDao userDao,
                    @Value("${netflow.analyze.updateMeanValueTimeMillis}") long updateMeanValueTimeMillis,
                    NetflowDao netflowDao,
                    UserOpenedPortsDao userOpenedPortsDao) {
        this.userDao = userDao;
        this.netflowDao = netflowDao;
        this.userOpenedPortsDao = userOpenedPortsDao;

        this.updateMeanValueTimeMillis = updateMeanValueTimeMillis;

        usersMap = new HashMap<>();
        // при инициализации берём сохранённые в бд ip юзверей
        List<String> userIpAddresses = userDao.findAll();
        for (String userIpAddress : userIpAddresses) {
            usersMap.put(userIpAddress, new User(userIpAddress, updateMeanValueTimeMillis));
        }
    }

    public void addUser(String ipAddress) {
        usersMap.putIfAbsent(ipAddress, new User(ipAddress,updateMeanValueTimeMillis));
        userDao.save(ipAddress);
    }

    public void deleteUser(String ipAddress) {
        usersMap.remove(ipAddress);

        userDao.remove(ipAddress);
        netflowDao.deleteUserFlowsByIp(ipAddress);
        userOpenedPortsDao.removeAll(ipAddress);
    }

    public void addUsers(List<String> usersIpAddresses) {
        for (String userIpAddress : usersIpAddresses) {
            usersMap.put(userIpAddress, new User(userIpAddress, updateMeanValueTimeMillis));
        }
        userDao.saveList(usersIpAddresses);
    }

    public User getUserByIpAddress(String ipAddress) {
        User user = usersMap.get(ipAddress);
        if (user == null) {
            System.err.println("User with ip address \"" + ipAddress + "\" not found!");
            return null;
        }
        return user;
    }

    public List<User> getUserList(){
        return new ArrayList<>(usersMap.values());
    }

    public boolean isExist(String ipAddress){
        return usersMap.containsKey(ipAddress);
    }
}