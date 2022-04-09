package com.zhmenko.model.user;

import com.zhmenko.model.netflow.NetflowPacket;
import com.zhmenko.model.netflow.Protocol;

import java.util.*;

public class UserStatistics {

    private UserStatistics(){
        throw new AssertionError();
    }

    /*public static String getAllUserStats(){
        String result = "";
        List<User> userList = User.getUserList();
        for (User user : userList) {
            result += "User " + user.getHostName() + ": " + user.getIpAddress() +'\n';
            result+="Средние значения пакетов:\n";
            for (Protocol protocol : Protocol.values()) {
                result += protocol + ": " + user.protocolFlowMeanValueHashMap.get(protocol)+'\n';
            }
            result += UserStatistics.getUserPacketStats(user)+'\n';
            for (Protocol protocol: Protocol.values()){
                result +="Количество уникальных "+protocol+"-портов назначения: " +
                        Util.getMaxDstPortCount(user.protocolsList.getUserProtocolList(protocol))+'\n';
            }
        }
        //result+=("Время до обновления: "+ new java.text.SimpleDateFormat("ss.S")
        //        .format(new java.util.Date(AnalyzeMainThread.getTimeLeftBfrUpdateMillis())));
        userList.clear();
        return  result;
    }*/
    public static String getAllUserStats(){
        String result = "";
        List<User> userList = User.getUserList();
        for (User user : userList) {
            result += "User " + user.getHostName() + ": " + user.getIpAddress() +'\n';
            result+="Средние значения пакетов:\n";
            Map<Protocol,Integer> flowMeanValues = user.getUserStatistic().getFlowMeanValues();
            if (flowMeanValues.size() > 0) {
                for (Protocol protocol : flowMeanValues.keySet()) {
                    result += protocol + ": " + flowMeanValues.get(protocol) + '\n';
                }
            }
            result += UserStatistics.getUserPacketStats(user)+'\n';
            Map<Protocol,Integer> dstPortValues = user.getUserStatistic().getProtocolUniqueDestinationPortCountMap();
            if ( dstPortValues != null && dstPortValues.size() > 0) {
                for (Protocol protocol : dstPortValues.keySet()) {
                    result += "Количество уникальных " + protocol + "-портов назначения: " + dstPortValues.get(protocol) + '\n';
                }
            }
        }
        //result+=("Время до обновления: "+ new java.text.SimpleDateFormat("ss.S")
        //        .format(new java.util.Date(AnalyzeMainThread.getTimeLeftBfrUpdateMillis())));
        userList.clear();
        return  result;
    }
/*    public static String getAllUserFlows(){
        String result ="";
        List<User> userList = User.getUserList();
        for (User user : userList) {
            result += "User " + user.getHostName() + ": " + user.getIpAddress() +'\n';
            List<List<NetflowPacket>> allProtocolLists = user.protocolsList.getUserAllProtocolLists();
            for (List<NetflowPacket> netflowPackets : allProtocolLists) {
                if (netflowPackets.size() == 0) {
                    continue;
                }
                result += "--------" + netflowPackets.get(0).getProtocol() + "-------------\n";
                result += Util.getNetflowList(netflowPackets);
            }
            allProtocolLists.clear();
        }
        userList.clear();
        return result;
    }*/

 public static String getUserPacketStats(User user){
     String result = "";
     List<List<NetflowPacket>> allProtocolsList = user.protocolsList.getUserAllProtocolLists();
     for (List<NetflowPacket> protocolList:allProtocolsList){
         if (protocolList.size() == 0) {continue;}
         result+="Количество "+protocolList.get(0).getProtocol()+" пакетов: "+ protocolList.size()+"\n";
     }
     allProtocolsList.clear();
     return result;
 }
/* public static int getUserUniqueProtocolDstPortsCount(User user, Protocol protocol){
     Set<String> portSet = new HashSet<>();
     if (user.protocolsList.getUserProtocolList(protocol).size() == 0) return 0;
     for (NetflowPacket flow:user.protocolsList.getUserProtocolList(protocol)){
         portSet.add(flow.getDstPort());
     }
     return portSet.size();
 }*/

}