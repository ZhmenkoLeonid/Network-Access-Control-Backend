package com.zhmenko.web.services.impl;

import com.zhmenko.dao.NetflowDao;
import com.zhmenko.model.netflow.NetflowPacket;
//import com.zhmenko.repositories.NetflowPacketRepository;
import com.zhmenko.model.netflow.Protocol;
import com.zhmenko.model.user.UserStatistic;
import com.zhmenko.web.services.NetflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NetflowServiceImpl implements NetflowService {
    @Autowired
    NetflowDao dao;

    @Override
    public List<NetflowPacket> getByIp(String ipAddress) throws SQLException {
        return dao.findByIp(ipAddress);
    }

    @Override
    public UserStatistic getUserStatisticByIpAddress(String ipAddress) throws SQLException {
        return dao.findUserStatisticByIpAddress(ipAddress);
    }

    @Override
    public void save(NetflowPacket packet) {
        dao.save(packet);
    }

    @Override
    public void saveList(List<NetflowPacket> packets) {
        dao.saveList(packets);
    }

    @Override
    public void saveProtocolListMap(Map<Protocol, List<NetflowPacket>> protocolListMap) {
        List<NetflowPacket> sumList = new ArrayList<>();
        for (List<NetflowPacket> list: protocolListMap.values()){
            sumList.addAll(list);
        }
        saveList(sumList);
    }

    @Override
    public List<NetflowPacket> getAll() {
        Iterable<NetflowPacket> iterable = dao.findAll();
        List<NetflowPacket> result = new ArrayList<>();
        //iterable.forEach(result::add);
        return result;
    }
}
