package com.zhmenko.ids.collector.factory.v5;

import com.zhmenko.data.nac.models.BlackListEntity;
import com.zhmenko.data.nac.models.NacRoleEntity;
import com.zhmenko.data.nac.models.NacUserEntity;
import com.zhmenko.data.nac.repository.BlackListRepository;
import com.zhmenko.data.nac.repository.NacRoleRepository;
import com.zhmenko.data.nac.repository.NacUserRepository;
import com.zhmenko.data.netflow.models.Protocol;
import com.zhmenko.data.netflow.models.TcpFlags;
import com.zhmenko.data.netflow.models.exception.UnsupportedProtocolException;
import com.zhmenko.data.netflow.models.exception.UserNotExistException;
import com.zhmenko.data.netflow.models.packet.NetflowPacketV5;
import com.zhmenko.data.netflow.models.user.NetflowUserList;
import com.zhmenko.data.security.models.SecurityUserEntity;
import com.zhmenko.data.security.repository.SecurityUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

@Component
@Slf4j
public class NetflowPacketFactoryV5 {
    private Map<String, Protocol> protocolHashMap;
    //private final BlackList blackList;

    private final BlackListRepository blackListRepository;
    private final NacUserRepository nacUserRepository;

    private final NacRoleRepository nacRoleRepository;

    private final SecurityUserRepository securityUserRepository;
    private final NetflowUserList netflowUserList;
    private final int meanValueIntervalMillis;

    public NetflowPacketFactoryV5(//BlackList blackList,
                                  BlackListRepository blackListRepository,
                                  NacUserRepository nacUserRepository,
                                  SecurityUserRepository securityUserRepository,
                                  NetflowUserList netflowUserList,
                                  NacRoleRepository nacRoleRepository,
                                  @Value("${netflow.analyze.updateMeanValueTimeMillis}") int meanValueIntervalMillis) {
        //this.blackList = blackList;
        this.blackListRepository = blackListRepository;
        this.nacUserRepository = nacUserRepository;
        this.securityUserRepository = securityUserRepository;
        this.nacRoleRepository = nacRoleRepository;
        this.netflowUserList = netflowUserList;

        this.meanValueIntervalMillis = meanValueIntervalMillis;
        this.protocolHashMap = new HashMap<>();
        for (Protocol value : Protocol.values()) {
            protocolHashMap.put(value.getValue(), value);
        }
    }

    public void addNetflowRecord(String srcIpAddress, String dstIpAddress, String protocol,
                                 String srcPort, String dstPort, Timestamp timestamp, int tcpFlags) {
        NacUserEntity nacUserEntity = nacUserRepository.findByIpAddress(srcIpAddress).orElse(null);
        // DEV CODE START
        if (!netflowUserList.isExistByIpAddress(srcIpAddress)) {
            if (nacUserEntity != null)
                if (nacUserEntity.getBlackListInfo().getIsBlocked()) return;
                else throw new IllegalStateException("Невозможная ситуация: юзера нет в локальном списке, " +
                        "но он есть в бд и не заблокирован!");
            nacUserEntity = createTestUser(srcIpAddress);
        } else if (nacUserEntity != null && nacUserEntity.getBlackListInfo().getIsBlocked())
            throw new IllegalStateException("Невозможная ситуация: юзер есть и в локальном списке, " +
                "и в бд, но он заблокирован!");
        // DEV CODE END
        // Если юзер                 заблокирован
        assert nacUserEntity != null;
        if (nacUserEntity.getBlackListInfo().getIsBlocked()
                // или сессия не активна, то не добавляем пакет
                || !netflowUserList.getUserSessionValideMap().get(nacUserEntity.getMacAddress())) return;
        // Проверяем, поддерживается ли протокол
        if (!protocolHashMap.containsKey(protocol)) {
            throw new UnsupportedProtocolException(protocol);
        }
        // Проверяем, есть ли у юзера доступ к порту, к которому он пытается обратиться
        List<Integer> userPorts = nacUserEntity.getPorts();
        if (!userPorts.contains(Integer.parseInt(dstPort))) return;
        // Добавляем пакет в список пользователя
        NetflowPacketV5 packetV5 = new NetflowPacketV5(srcIpAddress,
                dstIpAddress,
                Integer.parseInt(srcPort),
                Integer.parseInt(dstPort),
                protocolHashMap.get(protocol),
                timestamp,
                getTcpFlagsFromInt(tcpFlags));
        netflowUserList.getUserByIpAddress(srcIpAddress).getProtocolsFlowsList().addFlow(packetV5);
    }

    private static String getTcpFlagsFromInt(int tcpFlags) {
        if (tcpFlags < 0) throw new NumberFormatException("Число должно быть >= 0");
        StringBuilder result = new StringBuilder();
        String binarySet = Integer.toBinaryString(tcpFlags);
        for (int i = 0; i < binarySet.length(); i++) {
            if (binarySet.charAt(i) == '1') {
                result.append(TcpFlags.fromInt(binarySet.length() - 1 - i).name()).append(" ");
            }
        }
        if (binarySet.charAt(binarySet.length() - 1) == '1') {
            result.append(TcpFlags.fromInt(binarySet.length() - 1).name());
        }
        return result.toString();
    }

    private NacUserEntity createTestUser(String userIpAddress) {
        SecurityUserEntity securityUser = securityUserRepository.findByUsername("test_client")
                .orElseThrow(UserNotExistException::new);
        NacRoleEntity nacRoleEntity = nacRoleRepository.findByName("EXAMPLE_EMPLOYEE").orElse(null);
        NacUserEntity nacUserEntity = new NacUserEntity(randomMACAddress(),
                "test user",
                BlackListEntity.builder().isBlocked(false).build(),
                securityUser,
                userIpAddress,
                Collections.emptySet(),
                nacRoleEntity == null ? Collections.emptySet() : Set.of(nacRoleEntity));
        nacUserEntity.getBlackListInfo().setNacUserEntity(nacUserEntity);
        netflowUserList.addUser(nacUserEntity);
        log.info("Created TEST USER: " + nacUserEntity);
        return nacUserEntity;
    }

    private String randomMACAddress() {
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);

        macAddr[0] = (byte) (macAddr[0] & (byte) 254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

        StringBuilder sb = new StringBuilder(18);
        for (byte b : macAddr) {

            if (sb.length() > 0)
                sb.append(":");

            sb.append(String.format("%02x", b));
        }


        return sb.toString();
    }
}
