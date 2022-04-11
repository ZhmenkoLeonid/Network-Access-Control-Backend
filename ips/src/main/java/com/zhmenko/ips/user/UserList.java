package com.zhmenko.ips.user;

import com.zhmenko.dao.UserDao;
import com.zhmenko.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Scope("singleton")
public class UserList {
    private Map<String, User> usersMap;
    private UserDao userDao;
    private long updateMeanValueTimeMillis;

    public UserList(@Autowired UserDao userDao,
                    @Value("${netflow.analyze.updateMeanValueTimeMillis}") long updateMeanValueTimeMillis) {
        this.userDao = userDao;
        this.updateMeanValueTimeMillis = updateMeanValueTimeMillis;

        usersMap = new HashMap<>();
        // при инициализации берём сохранённые в бд ip юзверей
        List<String> userIpAddresses = userDao.findAll();
        for (String userIpAddress : userIpAddresses) {
            usersMap.put(userIpAddress, new User(userIpAddress, updateMeanValueTimeMillis));
        }
    }

    @Transactional
    public void addUser(String ipAddress) {
        usersMap.putIfAbsent(ipAddress, new User(ipAddress,updateMeanValueTimeMillis));
        userDao.save(ipAddress);
    }
    @Transactional
    public void deleteUser(String ipAddress) {
        usersMap.remove(ipAddress);
        userDao.remove(ipAddress);
    }
    @Transactional
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
