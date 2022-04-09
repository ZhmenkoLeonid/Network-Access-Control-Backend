package com.zhmenko.model.user;


import com.zhmenko.model.netflow.NetflowPacket;
import com.zhmenko.model.netflow.Protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class ProtocolsList {
    private Map<Protocol, List<NetflowPacket>> protocolListHashMap;
    private Map<Protocol, Boolean> busyListHashMap;

    public ProtocolsList() {
        protocolListHashMap = new ConcurrentHashMap<>();
        busyListHashMap = new ConcurrentHashMap<>();
        for (Protocol protocol : Protocol.values()) {
            protocolListHashMap.put(protocol, new CopyOnWriteArrayList<>());
            busyListHashMap.put(protocol, false);
        }
    }

    public void addFlow(NetflowPacket netflowPacket) {
        protocolListHashMap.get(netflowPacket.getProtocol()).add(netflowPacket);
    }

    public List<NetflowPacket> getUserProtocolList(Protocol protocol) {
        return protocolListHashMap.get(protocol);
    }

    public List<List<NetflowPacket>> getUserAllProtocolLists() {
        return new CopyOnWriteArrayList<>(protocolListHashMap.values());
    }

    public Map<Protocol, List<NetflowPacket>> getProtocolListHashMap() {
        return protocolListHashMap;
    }

    public boolean isEmpty() {
        for (Protocol protocol : Protocol.values()) {
            if (!protocolListHashMap.get(protocol).isEmpty()) return false;
        }
        return true;
    }

    public void clear() {
        for (Protocol protocol : Protocol.values()) {
            protocolListHashMap.get(protocol).clear();
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
