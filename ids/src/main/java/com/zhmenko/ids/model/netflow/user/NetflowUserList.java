package com.zhmenko.ids.model.netflow.user;

import com.zhmenko.ids.data.NacUserDao;
import com.zhmenko.ids.data.NetflowDao;
import com.zhmenko.ids.data.UserStatisticDao;
import com.zhmenko.ids.model.exception.UserNotExistException;
import com.zhmenko.ids.model.nac.NacUserDto;
import com.zhmenko.ids.model.netflow.user.tasks.RemoveUserTimerTask;
import com.zhmenko.router.SSH;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NetflowUserList {
    private final Map<String, NetflowUser> usersMap;
    private final Map<String, Timer> netflowUsersDeleteTimerMap;
    private final NacUserDao nacUserDao;
    private final SSH ssh;
    private final NetflowDao netflowDao;
    private final UserStatisticDao userStatisticDao;

    private long updateMeanValueTimeMillis;
    @Setter
    private long userSessionTTLMillis;


    @Autowired
    public NetflowUserList(@Value("${netflow.analyze.updateMeanValueTimeMillis}") long updateMeanValueTimeMillis,
                           @Value("${nac.session-ttl}") long userSessionTTLMillis,
                           SSH ssh,
                           NetflowDao netflowDao,
                           NacUserDao nacUserDao,
                           UserStatisticDao userStatisticDao) {
        this.nacUserDao = nacUserDao;
        this.netflowDao = netflowDao;
        this.userStatisticDao = userStatisticDao;
        this.ssh = ssh;

        this.updateMeanValueTimeMillis = updateMeanValueTimeMillis;
        this.userSessionTTLMillis = userSessionTTLMillis;
        this.usersMap = new HashMap<>();
        this.netflowUsersDeleteTimerMap = new HashMap<>();
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
            addUserTTLTimer(netflowUser);
        }
    }

    public NetflowUserList() {
        this.usersMap = new HashMap<>();
        this.userSessionTTLMillis = 10000;
        this.netflowUsersDeleteTimerMap = new HashMap<>();
        this.nacUserDao = null;
        this.netflowDao = null;
        this.userStatisticDao = null;
        this.ssh = null;
    }

    public boolean addUser(NetflowUser netflowUser) {
        Objects.requireNonNull(netflowUser);
        String macAddress = netflowUser.getMacAddress();
        if (usersMap.containsKey(macAddress)) {
            log.error("Не удалось добавить пользователя с mac адресом: " + macAddress + ", т.к. он уже существует");
            return false;
        }
        usersMap.put(netflowUser.getMacAddress(), netflowUser);
        addUserTTLTimer(netflowUser);
        return true;
        /*usersMap.putIfAbsent(macAddress, new User(macAddress, ipAddress, hostName, updateMeanValueTimeMillis));
        userDao.save(macAddress);*/
    }

    public boolean addUser(NacUserDto nacUserDto) {
        Objects.requireNonNull(nacUserDto);
        String macAddress = nacUserDto.getMacAddress();
        if (usersMap.containsKey(macAddress)) {
            log.error("Не удалось добавить пользователя с mac адресом: " + macAddress + ", т.к. он уже существует");
            return false;
        }
        NetflowUser netflowUser = new NetflowUser(macAddress, nacUserDto.getIpAddress(), nacUserDto.getHostname(), updateMeanValueTimeMillis);
        usersMap.put(netflowUser.getMacAddress(), netflowUser);
        nacUserDao.save(nacUserDto);
        addUserTTLTimer(netflowUser);
        return true;
    }

    public void deleteUser(String macAddress) {
        NacUserDto nacUserDto = nacUserDao.findByMacAddress(macAddress);
        if (nacUserDto == null)
            log.error("Не удалось удалить пользователя с mac адресом: " + macAddress + ", т.к. его не существует");
        usersMap.remove(macAddress);
        nacUserDao.removeByMacAddress(macAddress);
        //netflowDao.deleteUserFlowsByMacAddress(macAddress);
        netflowUsersDeleteTimerMap.remove(macAddress);
        ssh.denyUserPorts(nacUserDto.getIpAddress(), nacUserDto.getPorts());
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
        Objects.requireNonNull(ipAddress);
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
        Objects.requireNonNull(ipAddress);
        Collection<NetflowUser> users = usersMap.values();
        for (NetflowUser user : users) {
            if (user.getCurrentIpAddress().equals(ipAddress)) return true;
        }
        return false;
    }

    public boolean isExistByMacAddress(String macAddress) {
        Objects.requireNonNull(macAddress);
        Collection<NetflowUser> users = usersMap.values();
        for (NetflowUser user : users) {
            if (user.getMacAddress().equals(macAddress)) return true;
        }
        return false;
    }

    public NacUserDto updateUser(NetflowUser netflowUser) {
        Objects.requireNonNull(netflowUser);
        if (!usersMap.containsKey(netflowUser.getMacAddress()))
            throw new UserNotExistException("User cannot be updated because he doesnt exist!");
        String macAddress = netflowUser.getMacAddress();
        NacUserDto curUser = nacUserDao.findByMacAddress(macAddress);
        if (curUser == null)
            throw new IllegalStateException("Ошибка синхронизации списков пользователей");

        NetflowUser nUser = usersMap.get(netflowUser.getMacAddress());
        nUser.setHostname(netflowUser.getHostname());
        nUser.setCurrentIpAddress(netflowUser.getCurrentIpAddress());

        curUser.setHostname(netflowUser.getHostname());
        curUser.setIpAddress(netflowUser.getCurrentIpAddress());

        nacUserDao.update(curUser);
        return curUser;
    }

    public NacUserDto updateUser(NacUserDto nacUserDto) {
        Objects.requireNonNull(nacUserDto);
        String macAddress = nacUserDto.getMacAddress();
        if (!usersMap.containsKey(macAddress))
            throw new UserNotExistException("User cannot be updated because he doesnt exist!");
        NacUserDto curUser = nacUserDao.findByMacAddress(macAddress);
        if (curUser == null)
            throw new IllegalStateException("Ошибка синхронизации списков пользователей");

        NetflowUser netflowUser = usersMap.get(nacUserDto.getMacAddress());
        netflowUser.setHostname(nacUserDto.getHostname());
        netflowUser.setCurrentIpAddress(nacUserDto.getIpAddress());

        nacUserDao.update(nacUserDto);
        return curUser;
    }

    private void addUserTTLTimer(NetflowUser netflowUser) {
        Objects.requireNonNull(netflowUser);
        if (netflowUsersDeleteTimerMap.get(netflowUser) != null)
            throw new IllegalStateException("Невозможно добавить таймер, т.к. он уже существует " + netflowUser.toString());
        Timer newTimer = new Timer(true);
        newTimer.schedule(new RemoveUserTimerTask(this, netflowUser), userSessionTTLMillis);
        netflowUsersDeleteTimerMap.put(Objects.requireNonNull(netflowUser.getMacAddress()), newTimer);
    }

    public void updateUserTTLTimer(NetflowUser netflowUser) {
        Objects.requireNonNull(netflowUser);
        Objects.requireNonNull(netflowUser.getMacAddress());
        Timer userTTLTimer = netflowUsersDeleteTimerMap.get(netflowUser.getMacAddress());
        if (userTTLTimer == null)
            throw new UserNotExistException("Не удалось обновить таймер удаления, " +
                    "т.к. не существует пользователя " + netflowUser.toString());
        userTTLTimer.cancel();
        userTTLTimer.purge();
        Timer newTimer = new Timer(true);
        newTimer.schedule(new RemoveUserTimerTask(this, netflowUser), userSessionTTLMillis);
        netflowUsersDeleteTimerMap.replace(netflowUser.getMacAddress(), newTimer);
    }


    public void updateUserIpAddress(NetflowUser user, String newIpAddress) {

    }
}