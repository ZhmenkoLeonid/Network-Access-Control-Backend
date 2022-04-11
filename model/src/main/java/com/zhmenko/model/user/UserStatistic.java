package com.zhmenko.model.user;

import com.zhmenko.model.netflow.Protocol;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class UserStatistic {
    private String ipAddress;
    private Map<Protocol,Integer> protocolPacketCountMap;
    private Map<Protocol,Integer> protocolUniqueDestinationPortCountMap;
    private Date oldestPacketTime;
    private long meanValueIntervalMillis;

    public UserStatistic(long meanValueIntervalMillis){
        this.meanValueIntervalMillis = meanValueIntervalMillis;
    }

    public Map<Protocol,Integer> getFlowMeanValues() {
        Map<Protocol,Integer> resultMap = new HashMap<>();
        if (oldestPacketTime == null)
            return resultMap;
            ///throw new RuntimeException("Данных по юзеру с ip: " + ipAddress + " нет!");
        long pastTime = System.currentTimeMillis() - oldestPacketTime.getTime();
        long parts = pastTime / meanValueIntervalMillis;
        for (Protocol protocol: Protocol.values()){
            int meanValue = (int) (protocolPacketCountMap.get(protocol)/parts);
            resultMap.put(protocol,meanValue);
        }
        return resultMap;
    }
}
