package com.zhmenko.ids.model.netflow.user;


import com.zhmenko.ids.model.netflow.packet.NetflowPacket;
import com.zhmenko.ids.model.netflow.Protocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class ProtocolsFlowsList {
    private Map<Protocol, List<NetflowPacket>> protocolListHashMap;

    public ProtocolsFlowsList() {
        protocolListHashMap = new ConcurrentHashMap<>();
        for (Protocol protocol : Protocol.values()) {
            protocolListHashMap.put(protocol, new CopyOnWriteArrayList<>());
        }
    }

    public void addFlow(NetflowPacket netflowPacket) {
        protocolListHashMap.get(netflowPacket.getProtocol()).add(netflowPacket);
    }

    public List<List<NetflowPacket>> getUserAllProtocolLists() {
        return new ArrayList<>(protocolListHashMap.values());
    }

    public List<NetflowPacket> getAllFlows(){
        Collection<List<NetflowPacket>> protocolLists = protocolListHashMap.values();
        List<NetflowPacket> netflowPacketList = new ArrayList<>();
        for (List<NetflowPacket> protocolList : protocolLists) {
            netflowPacketList.addAll(protocolList);
        }
        return netflowPacketList;
    }

    public Map<Protocol, List<NetflowPacket>> getProtocolListHashMap() {
        return protocolListHashMap;
    }

    public boolean isEmpty() {
        for (List<NetflowPacket> lst : protocolListHashMap.values()) {
            if (!lst.isEmpty()) return false;
        }
        return true;
    }

    public void clear() {
        for (List<NetflowPacket> lst : protocolListHashMap.values()) {
            lst.clear();
        }
    }
    /*
    public static HashMap<String, HashMap<Protocol, List<NetflowPacket>>> getUserListHashMap() {
        return userListHashMap;
    }

    public List<NetflowPacket>[] getUserAllProtocolListsArray(){
        List<NetflowPacket>[] resultList = new ArrayList[Protocol.values().length];
        int i = 0;
        for (Protocol protocol: Protocol.values()){
            resultList[i] = getUserProtocolList(protocol);
            i++;
        }
        return resultList;
    }

    public void waitQueue(Protocol protocol){
        Boolean busy = busyListHashMap.get(protocol);
        try {
            while (busy) {
                Thread.sleep(300);
            }
            swapBusy(protocol);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private static void waitQueue(){
        try {
            while (busy) {
                Thread.sleep(300);
            }
            swapBusy();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private static void swapBusy(){
        busy = !busy;
    }

    public void swapBusy(Protocol protocol) {
        busyListHashMap.
                replace(protocol,!busyListHashMap.get(protocol));
    }
*/
}
