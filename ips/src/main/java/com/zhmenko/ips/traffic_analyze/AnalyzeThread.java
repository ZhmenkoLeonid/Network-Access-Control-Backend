package com.zhmenko.ips.traffic_analyze;

import com.jcraft.jsch.JSchException;
import com.zhmenko.ips.user.UserList;
import com.zhmenko.model.netflow.NetflowPacket;
import com.zhmenko.model.netflow.Protocol;
import com.zhmenko.model.user.User;
import com.zhmenko.ips.user.BlackList;
import com.zhmenko.ips.gui.Console;
import com.zhmenko.ips.router_interaction.SSH;
import com.zhmenko.ips.router_interaction.SSHCisco;
import com.zhmenko.ips.router_interaction.SSHKeenetic;
import com.zhmenko.web.services.NetflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.text.BadLocationException;
import java.sql.SQLException;
import java.util.*;
import java.io.IOException;

@Component
@Scope("singleton")
@Slf4j
//
@Order()
public class AnalyzeThread extends Thread {

    /* берём 3000 пакетов в час для каждого протокола как средние */
    private final double defaultMeanValueMillisMultiplier = 0.0008333;
    private Console console;

    private long defaultMeanValue;
    private static long timerExecuteTimeMillis;
    private SSH ssh;
    private NetflowService netflowService;
    private BlackList blackList;
    private UserList userList;

    private AnalyzeProperties properties;

    public AnalyzeThread(@Autowired AnalyzeProperties properties,
                         @Autowired NetflowService netflowService,
                         @Autowired Console console,
                         @Autowired BlackList blackList,
                         @Autowired UserList userList)
            throws InterruptedException, JSchException, IOException {
        this.properties = properties;
        this.blackList = blackList;
        this.userList = userList;
        this.netflowService = netflowService;
        this.console = console;
        this.defaultMeanValue = (long) (defaultMeanValueMillisMultiplier
                * properties.getUpdateMeanValueTimeMillis());
        switch (properties.getRouterType()) {
            case CISCO:
                ssh = new SSHCisco();
                break;
            case KEENETIC:
                ssh = new SSHKeenetic();
        }
        this.start();
    }
/*
    public AnalyzeThread(Router router)
            throws InterruptedException, JSchException, IOException {
        this.analyzeFrequencyMillis = 1000*5;
        //TODO рефактор к спринговскому
        this.updateMeanValueTimeMillis = NetflowPacketDeleteByTimeThread
                .currentThread().isAlive() ? 15*60*//*NetflowPacketDeleteByTimeThread
                .getLifetimeMillis()*//* : 10 * 60 * 1000;
        this.defaultMeanValue = (long)(this.defaultMeanValueMillisMultiplier
                * this.updateMeanValueTimeMillis);
        switch (router){
            case CISCO:
                ssh = new SSHCisco();
                break;
            case KEENETIC:
                ssh = new SSHKeenetic();
        }
    }*/

    @Override
    public void run() {
        //HashMap<Protocol,Long> protocolLongHashMap;
        while (true) {
            try {
                log.info("analyze invoke");
                for (User user : userList.getUserList()) {
                    // Получаем последнюю статистику, если она обновлялась
                    if (!user.getProtocolsFlowsList().isEmpty()) {
                        netflowService.saveProtocolListMap(user.getProtocolsFlowsList().getProtocolListHashMap());
                        user.getProtocolsFlowsList().clear();
                        log.info("save flows for user: " + user.getIpAddress());
                        user.updateUserStatistic(netflowService.getUserStatisticByIpAddress(user.getIpAddress()));
                    }
                    // тип от дудоса
                    checkUserMeanPacketValue(user);
                    // от сканирования портов
                    checkDstPortValues(user);
                }
                timerExecuteTimeMillis = properties.getAnalyzeFrequencyMillis() + System.currentTimeMillis();
                properties.setAnalyzeExecuteTime(timerExecuteTimeMillis);
                Thread.sleep(properties.getAnalyzeFrequencyMillis());
            } catch (InterruptedException | BadLocationException | SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /*private void updateMeanValueThread() {
        Long newMeanValue;
        while (true) {
            try {
                timerExecuteTimeMillis = System.currentTimeMillis() + updateMeanValueTimeMillis;
                Thread.sleep(updateMeanValueTimeMillis);
                List<User> userList = User.getUserList();
                for (User user : userList) {
                    for (Protocol protocol : Protocol.values()) {
                        newMeanValue = (user.protocolFlowMeanValueHashMap.get(protocol)
                                + user.protocolsList.getUserProtocolList(protocol)
                                .size()) / 2;
                        user.protocolFlowMeanValueHashMap.replace(
                                protocol,
                                (newMeanValue < defaultMeanValue) ?
                                        defaultMeanValue : newMeanValue);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/

    private void checkUserMeanPacketValue(User user) throws BadLocationException {
        Map<Protocol, Integer> userMeanValues = user.getUserStatistic().getFlowMeanValues();
        for (List<NetflowPacket> protocolList : user.getProtocolsFlowsList().getUserAllProtocolLists()) {
            if (protocolList.size() > 0)
                if (protocolList.size() >
                        userMeanValues
                                .get(protocolList.get(0).getProtocol()) * properties.getFlowMultiplierLimitation()) {
                    console.appendMsg("Пользователь " + user.getHostName() +
                            " с ip " + user.getIpAddress() + " превышает допустимое количество " +
                            protocolList.get(0).getProtocol() + " пакетов!");
                    String userIp = user.getIpAddress();
                    blackList.blockUser(userIp);
                    userList.deleteUser(userIp);
                    // ssh.denyUser(userIp);
                }
        }
    }

    private void checkDstPortValues(User user) throws BadLocationException {
        Map<Protocol, Integer> protUniqueDstPortCntMap = user.getUserStatistic().getProtocolUniqueDestinationPortCountMap();
        if (protUniqueDstPortCntMap == null) return;

        for (Protocol protocol : protUniqueDstPortCntMap.keySet()) {
            Integer protCnt = protUniqueDstPortCntMap.get(protocol);
            if (protCnt != null && protCnt > properties.getMaxUniqueDestinationPortCount()) {
                console.appendMsg("Пользователь " + user.getHostName() +
                        " с ip " + user.getIpAddress() + " превышает допустимое ("
                        + properties.getMaxUniqueDestinationPortCount() + ") количество обращений" +
                        " к уникальным портам по протоколу " + protocol + ": " + protCnt);
                String userIp = user.getIpAddress();
                blackList.blockUser(userIp);
                userList.deleteUser(userIp);
                //ssh.denyUser(userIp);
            }
        }
    }

    public static long getTimeLeftBfrUpdateMillis() {
        return timerExecuteTimeMillis - System.currentTimeMillis();
    }

    public long getDefaultMeanValue() {
        return defaultMeanValue;
    }
}