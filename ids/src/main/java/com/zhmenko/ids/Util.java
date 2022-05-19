package com.zhmenko.ids;


import com.zhmenko.ids.gui.Console;
import com.zhmenko.ids.model.netflow.packet.NetflowPacket;

import javax.swing.text.BadLocationException;
import java.util.*;

public class Util {

    private Util(){
        throw new AssertionError();
    }

    public static String getNeflowList (List<NetflowPacket> list){
        String result = "";
        if (list.size() > 50){
            for (int i = list.size()-50;i<list.size();i++){
                result+=list.get(i).toString()+'\n';
            }
            return result;
        }
        for (NetflowPacket flow: list) {
            result+=flow.toString()+'\n';
        }
        return result;
    }
    public static void printNetflowList(List<NetflowPacket> list, Console console) throws BadLocationException {
        if (list.size() > 50){
            for (int i = list.size()-50;i<list.size();i++){
                System.out.println(list.get(i).toString());
                console.appendMsg(list.get(i).toString());
            }
            return;
        }
        for (NetflowPacket flow: list) {
            System.out.println(flow.toString());
            console.appendMsg(flow.toString());
        }
    }
    /*public static long getMaxDstPortCount(List<NetflowPacket> netflowPackets){
        HashMap<String, HashSet<String>> uniqieDstPorts = new HashMap<>();
        String dstIpAddress;
        for (NetflowPacket packet:netflowPackets){
            if (!uniqieDstPorts.containsKey(dstIpAddress = packet.getDstIpAddress())){
                uniqieDstPorts.put(dstIpAddress,new HashSet<>());
            }
            uniqieDstPorts.get(dstIpAddress).add(packet.getDstPort());
        }
        List<Long> count= new ArrayList<>();

        // считаем кол-во уникальных портов для каждого ip адреса
        count.addAll(uniqieDstPorts.values().stream().map(value -> (long) value.size()).
                collect(Collectors.toList()));

        return count.size() > 0 ? Collections.max(count) : 0;
    }*/
}
