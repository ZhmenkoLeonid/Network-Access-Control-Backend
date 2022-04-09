package com.zhmenko.model.user;

import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class User {
    private String hostName;
    private String ipAddress;
    private static boolean busy = false;
    private static Map<String, User> userHashMap = new ConcurrentHashMap<>();
    /*    private Long defaultMeanValue;*/
    public ProtocolsList protocolsList;
    /*    public HashMap<Protocol,Long> protocolFlowMeanValueHashMap;*/
    UserStatistic userStatistic;

    public User(String hostName, String ipAddress, int meanValueIntervalMillis) {
        this.hostName = hostName;
        this.ipAddress = ipAddress;
        protocolsList = new ProtocolsList();
        userStatistic = new UserStatistic(meanValueIntervalMillis);
        /*        this.defaultMeanValue = defaultMeanValue;*/
        /*        protocolFlowMeanValueHashMap = new HashMap<>();*/
/*        for (Protocol protocol:Protocol.values()){
            protocolFlowMeanValueHashMap.put(protocol,this.defaultMeanValue);
        }
        userHashMap.put(ipAddress,this);*/
    }

    public User(String ipAddress, int meanValueIntervalMillis) {
        this.hostName = "Host" + (userHashMap.size() + 1);
        this.ipAddress = ipAddress;
        this.protocolsList = new ProtocolsList();
        userStatistic = new UserStatistic(meanValueIntervalMillis);
/*        this.protocolFlowMeanValueHashMap = new HashMap<>();
        for (Protocol protocol:Protocol.values()){
            this.protocolFlowMeanValueHashMap.put(protocol,3000L);
        }*/
        this.userHashMap.put(ipAddress, this);
    }

    public static Boolean isExist(String ipAddress) {
        return userHashMap.keySet().contains(ipAddress);
    }

    public static void deleteUser(String ipAddress) {
        if (userHashMap.remove(ipAddress) == null)
            System.err.println("User with ip address \"" + ipAddress + "\" not found!");
    }

    public static User getUserByIpAddress(String ipAddress) {
        User result = userHashMap.get(ipAddress);
        if (result == null) {
            System.err.println("User with ip address \"" + ipAddress + "\" not found!");
            return null;
        }
        return result;
    }
/*    public HashMap<Protocol, Long> getProtocolFlowMeanValueHashMap() {
        return protocolFlowMeanValueHashMap;
    }*/

    public static Map<String, User> getUserHashMap() {
        return userHashMap;
    }

    public void updateUserStatistic(UserStatistic userStatistic) {
        userStatistic.setMeanValueIntervalMillis(this.userStatistic.getMeanValueIntervalMillis());
        this.userStatistic = userStatistic;
    }

    public static List<User> getUserList() {
        return new CopyOnWriteArrayList<>(userHashMap.values());
    }

    @Override
    public String toString() {
        return "{" +
                "hostName=" + hostName +
                ", ipAddress=" + ipAddress +
                '}';
    }
}