package com.zhmenko.ids.traffic_analyze.analyzers;

import com.zhmenko.ids.models.ids.device.NetflowDevice;
import com.zhmenko.ids.models.ids.device.NetflowDeviceStatistic;
import com.zhmenko.ids.traffic_analyze.AnalyzeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
@Profile({"dev","prod"})
public class MeanPacketValueTrafficAnalyzer implements TrafficAnalyzer {
    private final AnalyzeProperties properties;

    @Override
    public List<String> analyze(NetflowDevice netflowDevice) {
        NetflowDeviceStatistic netflowDeviceStatistic = netflowDevice.getNetflowDeviceStatistic();
        long meanValueIntervalMillis = netflowDeviceStatistic.getMeanValueIntervalMillis();
        //TODO сделать дефолтное значение пакетов? Или оставь как есть подразумевая, что система должна "разогреться"
        //Смотрим, прошёл ли хотя бы 1 период (помимо последнего) для того, чтобы было с чем сравнивать (иначе сравнение не имеет смысла)
        Timestamp oldestPacketTime = netflowDeviceStatistic.getOldestPacketTime();
        if (oldestPacketTime == null /*||
                (Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))).getTime()
                        - oldestPacketTime.getTime()) < 2*meanValueIntervalMillis*/) {
            log.info("Пропуск "+ netflowDevice +", т.к. данных недостаточно!");
            return Collections.emptyList();
        }
        // Берём среднее количество пакетов пользователя за всё время, кроме последнего периода
        long userMeanPacketCount = netflowDeviceStatistic.getFlowMeanValues();
        //TODO надо начальное среднее делать всё-таки
        if (userMeanPacketCount == 0) userMeanPacketCount = 500;//return Collections.emptyList();
        // Берём среднее количество пакетов пользователя за последний период (напр. за последний час)
        long userLastMeanPacketCount = netflowDeviceStatistic.getLastPacketsCount();
        log.info(netflowDeviceStatistic.toString());
        // Получаем граничный множитель числа пакетов - число, определяющее,
        // во сколько раз допустимо превышать среднее число пакетов
        // Считаем максимально допустимое ограничение
        long flowLimit = userMeanPacketCount * properties.getFlowMultiplierLimitation();
        // Если выходим за границу - сообщаем о блокировке пользователя
        if (userLastMeanPacketCount > flowLimit) {
            String macAddress = netflowDevice.getMacAddress();
            String hostname = netflowDevice.getHostname();
            String notificationMsg = "MVE. Пользователь " + hostname
                    + " с mac=" + macAddress
                    + ", с ip=" + netflowDevice.getCurrentIpAddress()
                    + " превышает допустимое количество пакетов!";
            log.info(notificationMsg);
            return List.of(notificationMsg);
        }
        return Collections.emptyList();
    }
}
