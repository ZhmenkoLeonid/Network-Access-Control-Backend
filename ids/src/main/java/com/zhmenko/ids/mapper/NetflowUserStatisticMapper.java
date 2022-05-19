package com.zhmenko.ids.mapper;

import com.zhmenko.ids.model.netflow.user.NetflowUserList;
import com.zhmenko.ids.model.netflow.user.NetflowUserStatistic;
import com.zhmenko.ids.model.netflow.user.NetflowUserStatisticDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class NetflowUserStatisticMapper {
    private NetflowUserList netflowUserList;

    public NetflowUserStatisticDto build(NetflowUserStatistic userStatistic) {
        if (userStatistic.getMacAddress() == null) return null;
        return new NetflowUserStatisticDto(userStatistic.getMacAddress(),
                userStatistic.getFlowMeanValues(),
                userStatistic.getLastPacketsCount(),
                userStatistic.getMeanValueIntervalMillis(),
                userStatistic.getProtocolUniqueDestinationPortCountMap());
    }

    public Map<String, NetflowUserStatisticDto> build(List<NetflowUserStatistic> userStatisticList) {
        Map<String, NetflowUserStatisticDto> userStatisticMap = new HashMap<>();

        for (NetflowUserStatistic userStatistic : userStatisticList) {
            if (userStatistic.getMacAddress() == null) continue;
            NetflowUserStatisticDto userStatisticDto = new NetflowUserStatisticDto(
                    userStatistic.getMacAddress(),
                    userStatistic.getFlowMeanValues(),
                    userStatistic.getLastPacketsCount(),
                    userStatistic.getMeanValueIntervalMillis(),
                    userStatistic.getProtocolUniqueDestinationPortCountMap()
            );
            userStatisticMap.put(userStatistic.getMacAddress(), userStatisticDto);
        }

        return userStatisticMap;
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
    /*public String getAllUserStats() {
        StringBuilder result = new StringBuilder();
        List<NetflowUser> usrList = netflowUserList.getUserList();
        for (NetflowUser user : usrList) {
            result.append("User=").append(user.getHostname())
                    .append(", Mac address=").append(user.getMacAddress())
                    .append("\nСредние значения пакетов:\n")
                    .append("Среднее значение: ").append(user.getNetflowUserStatistic().getFlowMeanValues())
                    .append('\n')
                    .append("Число пакетов за последний период: ").append(user.getNetflowUserStatistic().getLastPacketsCount())
                    .append('\n');

*//*            if (flowMeanValues.size() > 0) {
                for (Protocol protocol : flowMeanValues.keySet()) {
                    result.append(protocol).append(": ").append(flowMeanValues.get(protocol)).append('\n');
                }
            }*//*
            Map<Protocol,Long> dstPortValues = user.getNetflowUserStatistic().getProtocolUniqueDestinationPortCountMap();
            if ( dstPortValues != null && dstPortValues.size() > 0) {
                for (Protocol protocol : dstPortValues.keySet()) {
                    result.append("Количество уникальных ")
                            .append(protocol)
                            .append("-портов назначения: ")
                            .append(dstPortValues.get(protocol))
                            .append('\n');
                }
            }
        }
        //result+=("Время до обновления: "+ new java.text.SimpleDateFormat("ss.S")
        //        .format(new java.util.Date(AnalyzeMainThread.getTimeLeftBfrUpdateMillis())));
        return result.toString();
    }*/
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
/* public static int getUserUniqueProtocolDstPortsCount(User user, Protocol protocol){
     Set<String> portSet = new HashSet<>();
     if (user.protocolsList.getUserProtocolList(protocol).size() == 0) return 0;
     for (NetflowPacket flow:user.protocolsList.getUserProtocolList(protocol)){
         portSet.add(flow.getDstPort());
     }
     return portSet.size();
 }*/

}