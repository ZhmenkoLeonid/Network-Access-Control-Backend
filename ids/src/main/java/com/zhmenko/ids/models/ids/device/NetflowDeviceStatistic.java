package com.zhmenko.ids.models.ids.device;

import com.zhmenko.ids.models.ids.Protocol;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Data
@NoArgsConstructor
public class NetflowDeviceStatistic {
    private String macAddress;
    private long packetsCount;
    private long lastPacketsCount;
    private Map<Protocol,Long> protocolUniqueDestinationPortCountMap;
    private Timestamp oldestPacketTime;
    private long meanValueIntervalMillis;

    public NetflowDeviceStatistic(long meanValueIntervalMillis){
        this.meanValueIntervalMillis = meanValueIntervalMillis;
    }

    // тут мы считаем среднее значение пакетов за период времени
    // [время старейшего пакета; время новейшего пакета - meanValueIntervalMillis]
    // Например, если считаем среднее число пакетов в час, то в данном методе вычисляется среднее число пакетов за время
    // [время старейшего пакета; 'время новейшего пакета' - 'час'], т.е мы не учитываем пакеты за последний час
    public long getFlowMeanValues() {
        if (oldestPacketTime == null) return 0;
        //throw new RuntimeException("Данных по юзеру с ip: " + ipAddress + " нет!");
        long pastTime = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))).getTime() - oldestPacketTime.getTime();
        // - 1 , т.к. мы вычисляем пакеты до интервала, с которым будем сравнивать при анализе
        long parts = pastTime / meanValueIntervalMillis - 1;
        return parts == 0 ? 0 : packetsCount/parts;
    }
}
