package com.zhmenko.model.user;

import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class User {
    private String hostName;
    private String ipAddress;
    //private static Map<String, User> userHashMap = new ConcurrentHashMap<>();
    /*    private Long defaultMeanValue;*/
    private ProtocolsFlowsList protocolsFlowsList;
    /*    public HashMap<Protocol,Long> protocolFlowMeanValueHashMap;*/
    private UserStatistic userStatistic;

    public User(String hostName, String ipAddress, int meanValueIntervalMillis) {
        this.hostName = hostName;
        this.ipAddress = ipAddress;

        this.protocolsFlowsList = new ProtocolsFlowsList();
        this.userStatistic = new UserStatistic(meanValueIntervalMillis);
        //userHashMap.put(ipAddress,this);
    }

    public User(String ipAddress, long meanValueIntervalMillis) {
        this.hostName = "Host";
        this.ipAddress = ipAddress;
        this.protocolsFlowsList = new ProtocolsFlowsList();
        userStatistic = new UserStatistic(meanValueIntervalMillis);
        //userHashMap.put(izpAddress, this);
    }

    /*public static Boolean isExist(String ipAddress) {
        return userHashMap.keySet().contains(ipAddress);
    }*/

  /*  public static void deleteUser(String ipAddress) {
        if (userHashMap.remove(ipAddress) == null)
            System.err.println("User with ip address \"" + ipAddress + "\" not found!");
    }*/

/*    public static User getUserByIpAddress(String ipAddress) {
        User result = userHashMap.get(ipAddress);
        if (result == null) {
            System.err.println("User with ip address \"" + ipAddress + "\" not found!");
            return null;
        }
        return result;
    }*/
/*    public HashMap<Protocol, Long> getProtocolFlowMeanValueHashMap() {
        return protocolFlowMeanValueHashMap;
    }*/

  /*  public static Map<String, User> getUserHashMap() {
        return userHashMap;
    }*/

    public void updateUserStatistic(UserStatistic userStatistic) {
        userStatistic.setMeanValueIntervalMillis(this.userStatistic.getMeanValueIntervalMillis());
        this.userStatistic = userStatistic;
    }

/*
    public static List<User> getUserList() {
        return new CopyOnWriteArrayList<>(userHashMap.values());
    }
*/

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) return false;
        User user = (User) obj;

        if (user.ipAddress != this.ipAddress
                || user.hostName != this.hostName) return false;
        return true;
    }

    @Override
    public String toString() {
        return "{" +
                "hostName=" + hostName +
                ", ipAddress=" + ipAddress +
                '}';
    }
}