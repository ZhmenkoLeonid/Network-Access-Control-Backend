/*
package com.zhmenko.ips.traffic_analyze;

import com.zhmenko.model.netflow.NetflowPacket;
import com.zhmenko.model.netflow.NetflowPacketV5;
import com.zhmenko.model.user.ProtocolsFlowsList;
import com.zhmenko.model.user.User;
import com.zhmenko.dao.NetflowDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

//@Component
@Slf4j
public class NetflowPacketSaveByTimeThread extends Thread {

    private long lifetimeMillis;
    private long waitTimer;
    private NetflowDao netflowDao;

    public NetflowPacketSaveByTimeThread(
            @Value("${netflow.saveFrequencyTime}")long lifetimeMillis
            , @Autowired NetflowDao netflowDao) {
        this.lifetimeMillis = lifetimeMillis;
        this.netflowDao = netflowDao;
        start();
    }
*/
/*
    public NetflowPacketDeleteByTimeThread() {
        // 30 sec - default value
        lifetimeMillis = 30 * 1000;
        start();
    }*//*


    @Override
    public void run() {
        try {
            while (true) {
                List<User> userList = User.getUserList();
                List<NetflowPacket> netflowPackets = new ArrayList<>();
                for (User user : userList) {
                    netflowPackets.addAll(deleteUserFlows(user));
                }
                Date oldestTimestamp = getOldestTimestamp();
                waitTimer = oldestTimestamp == null ? lifetimeMillis :
                        oldestTimestamp.getTime() - System.currentTimeMillis() + lifetimeMillis + 1;

                if (netflowPackets.size() > 0)
                    log.info("save flows: \n"+
                            netflowPackets.stream()
                                    .map(NetflowPacket::toString)
                                    .collect(Collectors.joining("\n"))
                    );

                netflowDao.saveList(netflowPackets);

                if (waitTimer < 0) {
                    Thread.sleep(2000);
                } else {
                    Thread.sleep(waitTimer);
                }
            }
        } catch (InterruptedException | ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    private List<NetflowPacketV5> deleteUserFlows(User user) {
        List<List<NetflowPacket>> userAllProtocolLists = user.getProtocolsFlowsList().getUserAllProtocolLists();
        List<NetflowPacketV5> deletedPackets = new ArrayList<>();
        for (List<NetflowPacket> protocolList : userAllProtocolLists) {
            for (NetflowPacket packet : protocolList) {
                deletedPackets.add(new NetflowPacketV5((NetflowPacketV5) packet));
                protocolList.remove(packet);
            }
        }
        return deletedPackets;
    }

    private Date getOldestTimestamp() {
        List<ProtocolsFlowsList> protocolsFlowsLists = new ArrayList<>(User.getUserHashMap().values()
                .stream()
                .map(user -> user.getProtocolsFlowsList())
                .collect(Collectors.toList())
        );
        List<Date> minTimestamp = new ArrayList<>();
        for (ProtocolsFlowsList protocolsFlowsList : protocolsFlowsLists) {
            for (List<NetflowPacket> packetList : protocolsFlowsList.getUserAllProtocolLists()) {
                if (packetList.size() == 0) continue;
                minTimestamp.add(packetList.get(0).getTimestamp());
            }
        }
        return minTimestamp.stream().min(Date::compareTo).orElse(null);
    }
}*/
