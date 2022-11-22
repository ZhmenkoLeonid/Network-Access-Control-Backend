package com.zhmenko.ids.traffic_analyze.analyzers;

import com.zhmenko.data.netflow.models.user.NetflowUser;
import com.zhmenko.data.netflow.models.user.NetflowUserStatistic;
import com.zhmenko.ids.traffic_analyze.AnalyzeProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Component
public class MeanPacketValueTrafficAnalyzer implements TrafficAnalyzer {
    private AnalyzeProperties properties;

    @Override
    public List<String> analyze(NetflowUser netflowUser) {
        NetflowUserStatistic netflowUserStatistic = netflowUser.getNetflowUserStatistic();
        long meanValueIntervalMillis = netflowUserStatistic.getMeanValueIntervalMillis();
        //TODO сделать дефолтное значение пакетов? Или оставь как есть подразумевая, что система должна "разогреться"
        //Смотрим, прошёл ли хотя бы 1 период (помимо последнего) для того, чтобы было с чем сравнивать (иначе сравнение не имеет смысла)
        Timestamp oldestPacketTime = netflowUserStatistic.getOldestPacketTime();
        if (oldestPacketTime == null ||
                (Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))).getTime()
                        - oldestPacketTime.getTime()) < 2*meanValueIntervalMillis) {
            log.debug("Пропуск "+ netflowUser +", т.к. данных недостаточно!");
            return Collections.emptyList();
        }
        // Берём среднее количество пакетов пользователя за всё время, кроме последнего периода
        long userMeanPacketCount = netflowUserStatistic.getFlowMeanValues();
        //TODO надо начальное среднее делать всё-таки
        if (userMeanPacketCount == 0) return Collections.emptyList();
        // Берём среднее количество пакетов пользователя за последний период (напр. за последний час)
        long userLastMeanPacketCount = netflowUserStatistic.getLastPacketsCount();
        // Получаем граничный множитель числа пакетов - число, определяющее,
        // во сколько раз допустимо превышать среднее число пакетов
        // Считаем максимально допустимое ограничение
        long flowLimit = userMeanPacketCount * properties.getFlowMultiplierLimitation();
        // Если выходим за границу - сообщаем о блокировке пользователя
        if (userLastMeanPacketCount > flowLimit) {
            String macAddress = netflowUser.getMacAddress();
            String hostname = netflowUser.getHostname();
            String notificationMsg = "MVE. Пользователь " + hostname
                    + " с mac=" + macAddress
                    + ", с ip=" + netflowUser.getCurrentIpAddress()
                    + " превышает допустимое количество пакетов!";
            log.info(notificationMsg);
            return List.of(notificationMsg);
        }
        return Collections.emptyList();
    }
}
