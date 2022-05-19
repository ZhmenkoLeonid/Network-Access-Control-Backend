package com.zhmenko.ids.model.netflow.user;

import com.zhmenko.ids.data.NacUserDao;
import com.zhmenko.ids.data.NetflowDao;
import com.zhmenko.ids.data.UserStatisticDao;
import com.zhmenko.ids.model.exception.UserNotExistException;
import com.zhmenko.ids.model.nac.NacUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NetflowUserList {
    private final Map<String, NetflowUser> usersMap;
    private final NacUserDao nacUserDao;
    private final NetflowDao netflowDao;
    private final UserStatisticDao userStatisticDao;

    private long updateMeanValueTimeMillis;

    public NetflowUserList(@Value("${netflow.analyze.updateMeanValueTimeMillis}") long updateMeanValueTimeMillis,
                           NetflowDao netflowDao,
                           NacUserDao nacUserDao,
                           UserStatisticDao userStatisticDao) {
        this.nacUserDao = nacUserDao;
        this.netflowDao = netflowDao;
        this.userStatisticDao = userStatisticDao;

        this.updateMeanValueTimeMillis = updateMeanValueTimeMillis;

        usersMap = new HashMap<>();
        // при инициализации берём сохранённых в бд юзверей
        List<NetflowUser> usersDtos = nacUserDao.findAll().stream().map(
                nacUser ->
                        new NetflowUser(nacUser.getMacAddress(),
                                nacUser.getIpAddress(),
                                nacUser.getHostname(),
                                updateMeanValueTimeMillis))
                .collect(Collectors.toList());

        for (NetflowUser usersDto : usersDtos) {
            NetflowUserStatistic userStatistic = userStatisticDao
                    .findUserStatisticByMacAddress(usersDto.getMacAddress(), updateMeanValueTimeMillis / 1000);
            if (userStatistic != null) usersDto.updateUserStatistic(userStatistic);
        }

        for (NetflowUser netflowUser : usersDtos) {
            log.info("Add user: " + netflowUser.toString());
            usersMap.put(netflowUser.getMacAddress(), netflowUser);
        }
    }

    public boolean addUser(NetflowUser netflowUser) {
        String macAddress = netflowUser.getMacAddress();
        if (usersMap.containsKey(macAddress)) {
            log.error("Не удалось добавить пользователя с mac адресом: " + macAddress + ", т.к. он уже существует");
            return false;
        }
        usersMap.put(netflowUser.getMacAddress(), netflowUser);
        return true;
        /*usersMap.putIfAbsent(macAddress, new User(macAddress, ipAddress, hostName, updateMeanValueTimeMillis));
        userDao.save(macAddress);*/
    }

    public boolean addUser(NacUserDto nacUserDto) {
        String macAddress = nacUserDto.getMacAddress();
        if (usersMap.containsKey(macAddress)) {
            log.error("Не удалось добавить пользователя с mac адресом: " + macAddress + ", т.к. он уже существует");
            return false;
        }
        NetflowUser netflowUser = new NetflowUser(macAddress, nacUserDto.getIpAddress(), nacUserDto.getHostname(), updateMeanValueTimeMillis);
        usersMap.put(netflowUser.getMacAddress(), netflowUser);
        nacUserDao.save(nacUserDto);
        return true;
    }

    public void deleteUser(String macAddress) {
        usersMap.remove(macAddress);
        nacUserDao.removeByMacAddress(macAddress);
        netflowDao.deleteUserFlowsByMacAddress(macAddress);
    }
/*
    public void addUsers(List<NetflowUser> users) {
        //List<String> newMacAddresses = new ArrayList<>();
        List<String> existingMacAddresses = this.usersMap.values().stream()
                .map(NetflowUser::getMacAddress)
                .collect(Collectors.toList());
        for (NetflowUser netflowUser : users) {
            String macAddress = netflowUser.getMacAddress();
            if (existingMacAddresses.contains(macAddress)) {
                continue;
            }
            existingMacAddresses.add(macAddress);

            String hostName = netflowUser.getHostname();
            String ipAddress = netflowUser.getIpAddress();

            usersMap.put(macAddress, new NetflowUser(macAddress, ipAddress, hostName, updateMeanValueTimeMillis));
        }

        List<NacUser> nacUsers = users.stream().map(netflowUser ->
                new NacUser(netflowUser.getMacAddress(),
                netflowUser.getHostname(),
                netflowUser.getIpAddress())).collect(Collectors.toList());

        nacUserDao.saveList(nacUsers);
    }*/

    public NetflowUser getUserByMacAddress(String macAddress) {
        NetflowUser user = usersMap.get(macAddress);
        if (user == null) {
            log.error("User with mac address \"" + macAddress + "\" not found!");
            return null;
        }
        return user;
    }

    public NetflowUser getUserByIpAddress(String ipAddress) {
        Collection<NetflowUser> users = usersMap.values();
        for (NetflowUser user : users) {
            if (user.getCurrentIpAddress().equals(ipAddress)) return user;
        }
        return null;
    }

    public List<NetflowUser> getUserList() {
        return new ArrayList<>(usersMap.values());
    }

    public boolean isExistByIpAddress(String ipAddress) {
        Collection<NetflowUser> users = usersMap.values();
        for (NetflowUser user : users) {
            if (user.getCurrentIpAddress().equals(ipAddress)) return true;
        }
        return false;
    }

    public boolean isExistByMacAddress(String macAddress) {
        Collection<NetflowUser> users = usersMap.values();
        for (NetflowUser user : users) {
            if (user.getMacAddress().equals(macAddress)) return true;
        }
        return false;
    }

    public void updateUser(NetflowUser user) {
        if (usersMap.containsKey(user.getMacAddress())) {
            usersMap.replace(user.getMacAddress(), user);
        } else {
            throw new UserNotExistException("users cannot be updated because he doesnt exist!");
        }
    }

    public NacUserDto updateUser(NacUserDto nacUserDto) {
        String macAddress = nacUserDto.getMacAddress();
        NacUserDto curUser = nacUserDao.findByMacAddress(macAddress);
        if (curUser == null || !usersMap.containsKey(nacUserDto.getMacAddress()))
            return null;

        NetflowUser netflowUser = usersMap.get(nacUserDto.getMacAddress());
        netflowUser.setHostname(nacUserDto.getHostname());
        netflowUser.setCurrentIpAddress(nacUserDto.getIpAddress());

        nacUserDao.update(nacUserDto);
        return curUser;
    }

    public void updateUserIpAddress(NetflowUser user, String newIpAddress) {

    }
}