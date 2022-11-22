package com.zhmenko.data.netflow.models.user;

import com.zhmenko.data.nac.NetflowDao;
import com.zhmenko.data.nac.UserStatisticDao;
import com.zhmenko.data.nac.models.NacUserEntity;
import com.zhmenko.data.nac.repository.NacUserRepository;
import com.zhmenko.data.netflow.models.exception.UserNotExistException;
import com.zhmenko.data.netflow.models.user.tasks.UserSessionExpiredTimerTask;
import com.zhmenko.router.SSH;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NetflowUserList {
    private final Map<String, NetflowUser> usersMap;
    private final Map<String, Timer> netflowUsersDeleteTimerMap;

    private final Map<String, Boolean> userSessionValideMap;
    //private final NacUserDao nacUserDao;

    private final NacUserRepository nacUserRepository;
    @Qualifier("keenetic")
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
                           NacUserRepository nacUserRepository,
                           UserStatisticDao userStatisticDao) {
        this.nacUserRepository = nacUserRepository;
        this.netflowDao = netflowDao;
        this.userStatisticDao = userStatisticDao;
        this.ssh = ssh;

        this.updateMeanValueTimeMillis = updateMeanValueTimeMillis;
        this.userSessionTTLMillis = userSessionTTLMillis;
        this.usersMap = new HashMap<>();
        this.netflowUsersDeleteTimerMap = new HashMap<>();
        this.userSessionValideMap = new HashMap<>();
        // при инициализации берём сохранённых в бд юзверей
        List<NetflowUser> usersDtos = nacUserRepository.findAll().stream()
                // TODO сделать отдельный запрос
                .filter(user -> !user.getBlackListInfo().getIsBlocked())
                .map(nacUser -> new NetflowUser(nacUser.getMacAddress(),
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
            // TODO должна быть авторизация,
            //  поэтому в конечной версии надо убрать вызов метода тут
            addUserTTLTimer(netflowUser);
        }
    }

    public NetflowUserList() {
        this.usersMap = new HashMap<>();
        this.userSessionTTLMillis = 10000;
        this.netflowUsersDeleteTimerMap = new HashMap<>();
        this.userSessionValideMap = new HashMap<>();
        this.nacUserRepository = null;
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

    public boolean addUser(NacUserEntity nacUserEntity) {
        Objects.requireNonNull(nacUserEntity);
        String macAddress = nacUserEntity.getMacAddress();
        if (usersMap.containsKey(macAddress)) {
            log.error("Не удалось добавить пользователя с mac адресом: " + macAddress + ", т.к. он уже существует");
            return false;
        }
        NetflowUser netflowUser = new NetflowUser(macAddress, nacUserEntity.getIpAddress(), nacUserEntity.getHostname(), updateMeanValueTimeMillis);
        usersMap.put(netflowUser.getMacAddress(), netflowUser);
        nacUserRepository.save(nacUserEntity);
        addUserTTLTimer(netflowUser);
        return true;
    }

    public void deleteUser(String macAddress) {
        NacUserEntity nacUserEntity = nacUserRepository.findByMacAddress(macAddress);
        if (nacUserEntity == null) {
            log.error("Не удалось удалить пользователя с mac адресом: " + macAddress + ", т.к. его не существует");
            return;
        }
        usersMap.remove(macAddress);
        nacUserRepository.removeByMacAddress(macAddress);
        //netflowDao.deleteUserFlowsByMacAddress(macAddress);
        netflowUsersDeleteTimerMap.remove(macAddress);
        ssh.denyUserPorts(nacUserEntity.getIpAddress(), nacUserEntity.getPorts());
    }

    public NetflowUser removeUserFromLocalNetflowListByMacAddress(String macAddress) {
        netflowUsersDeleteTimerMap.remove(macAddress);
        return usersMap.remove(macAddress);
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

    public Map<String, Boolean> getUserSessionValideMap() {
        return userSessionValideMap;
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

    public NacUserEntity updateUser(NetflowUser netflowUser) {
        Objects.requireNonNull(netflowUser);
        if (!usersMap.containsKey(netflowUser.getMacAddress()))
            throw new UserNotExistException("User cannot be updated because he doesnt exist!");
        String macAddress = netflowUser.getMacAddress();
        NacUserEntity curUser = nacUserRepository.findByMacAddress(macAddress);
        if (curUser == null)
            throw new IllegalStateException("Ошибка синхронизации списков пользователей");

        NetflowUser nUser = usersMap.get(netflowUser.getMacAddress());
        nUser.setHostname(netflowUser.getHostname());
        nUser.setCurrentIpAddress(netflowUser.getCurrentIpAddress());

        curUser.setHostname(netflowUser.getHostname());
        curUser.setIpAddress(netflowUser.getCurrentIpAddress());

        nacUserRepository.save(curUser);
        return curUser;
    }

    public NacUserEntity updateUser(NacUserEntity nacUserEntity) {
        Objects.requireNonNull(nacUserEntity);
        String macAddress = nacUserEntity.getMacAddress();
        if (!usersMap.containsKey(macAddress))
            throw new UserNotExistException("User cannot be updated because he doesnt exist!");
        NacUserEntity curUser = nacUserRepository.findByMacAddress(macAddress);
        if (curUser == null)
            throw new IllegalStateException("Ошибка синхронизации списков пользователей");

        NetflowUser netflowUser = usersMap.get(nacUserEntity.getMacAddress());
        netflowUser.setHostname(nacUserEntity.getHostname());
        netflowUser.setCurrentIpAddress(nacUserEntity.getIpAddress());

        nacUserRepository.save(nacUserEntity);
        return curUser;
    }

    private void addUserTTLTimer(NetflowUser netflowUser) {
        Objects.requireNonNull(netflowUser);
        if (netflowUsersDeleteTimerMap.get(netflowUser.getMacAddress()) != null)
            throw new IllegalStateException("Невозможно добавить таймер, т.к. он уже существует " + netflowUser);
        Timer newTimer = new Timer(true);
        newTimer.schedule(new UserSessionExpiredTimerTask(this, netflowUser), userSessionTTLMillis);
        netflowUsersDeleteTimerMap.put(Objects.requireNonNull(netflowUser.getMacAddress()), newTimer);
        userSessionValideMap.put(netflowUser.getMacAddress(), true);
    }

    public void updateUserTTLTimer(NetflowUser netflowUser) {
        Objects.requireNonNull(netflowUser);
        Objects.requireNonNull(netflowUser.getMacAddress());
        Timer userTTLTimer = netflowUsersDeleteTimerMap.get(netflowUser.getMacAddress());
        if (userTTLTimer == null)
            throw new UserNotExistException("Не удалось обновить таймер удаления, " +
                    "т.к. не существует пользователя " + netflowUser);
        userTTLTimer.cancel();
        userTTLTimer.purge();
        Timer newTimer = new Timer(true);
        newTimer.schedule(new UserSessionExpiredTimerTask(this, netflowUser), userSessionTTLMillis);

        netflowUsersDeleteTimerMap.replace(netflowUser.getMacAddress(), newTimer);
    }


    public void updateUserIpAddress(NetflowUser user, String newIpAddress) {
        throw new NotYetImplementedException();
    }
}