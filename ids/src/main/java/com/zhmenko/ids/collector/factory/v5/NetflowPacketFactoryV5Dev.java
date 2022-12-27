package com.zhmenko.ids.collector.factory.v5;

import com.zhmenko.data.nac.models.UserBlockInfoEntity;
import com.zhmenko.data.nac.models.UserDeviceEntity;
import com.zhmenko.data.nac.repository.UserDeviceRepository;
import com.zhmenko.data.netflow.models.Protocol;
import com.zhmenko.data.netflow.models.TcpFlags;
import com.zhmenko.data.netflow.models.device.NetflowDevice;
import com.zhmenko.data.netflow.models.device.NetflowDeviceList;
import com.zhmenko.data.netflow.models.exception.UnsupportedProtocolException;
import com.zhmenko.data.netflow.models.exception.UserNotExistException;
import com.zhmenko.data.netflow.models.packet.NetflowPacketV5;
import com.zhmenko.data.security.models.SecurityUserEntity;
import com.zhmenko.data.security.repository.SecurityUserRepository;
import com.zhmenko.router.SSH;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

@Component
@Slf4j
@Profile("dev")
public class NetflowPacketFactoryV5Dev implements NetflowPacketFactoryV5{
    private final Map<String, Protocol> protocolHashMap;
    private final UserDeviceRepository userDeviceRepository;
    private final SecurityUserRepository securityUserRepository;
    private final NetflowDeviceList netflowDeviceList;

    private final SSH ssh;

    public NetflowPacketFactoryV5Dev(UserDeviceRepository userDeviceRepository,
                                     SecurityUserRepository securityUserRepository,
                                     NetflowDeviceList netflowDeviceList,
                                     @Qualifier("keenetic") SSH ssh) {
        this.userDeviceRepository = userDeviceRepository;
        this.securityUserRepository = securityUserRepository;
        this.netflowDeviceList = netflowDeviceList;
        this.ssh = ssh;

        this.protocolHashMap = new HashMap<>();
        for (Protocol value : Protocol.values()) {
            protocolHashMap.put(value.getValue(), value);
        }
    }

    public void addNetflowRecord(String srcIpAddress, String dstIpAddress, String protocol,
                                 String srcPort, String dstPort, Timestamp timestamp, int tcpFlags) {
        NetflowDevice user = netflowDeviceList.getUserByIpAddress(srcIpAddress);
        if (user == null) {
            Optional<UserDeviceEntity> nacUserEntity = userDeviceRepository.findByIpAddress(srcIpAddress);
            if (nacUserEntity.isPresent())
                if (nacUserEntity.get().getBlackListInfo().getIsBlocked()) return;
                else return;
            user = createTestDevice(srcIpAddress);
        }
        // DEV CODE END
        // Если юзер заблокирован
        if (user.getBlockedState()
                // или сессия не активна, то не добавляем пакет
                || !user.getDeviceSessionInfo().isSessionActiveState()) return;
        // Проверяем, поддерживается ли протокол
        if (!protocolHashMap.containsKey(protocol)) {
            throw new UnsupportedProtocolException(protocol);
        }
        // Проверяем, есть ли у юзера доступ к порту, к которому он пытается обратиться
        Set<Integer> userPorts = user.getOpenedPorts();
        if (!userPorts.contains(Integer.parseInt(dstPort))) return;
        // Добавляем пакет в список пользователя
        NetflowPacketV5 packetV5 = new NetflowPacketV5(srcIpAddress,
                dstIpAddress,
                Integer.parseInt(srcPort),
                Integer.parseInt(dstPort),
                protocolHashMap.get(protocol),
                timestamp,
                getTcpFlagsFromInt(tcpFlags));
        user.getProtocolsFlowsList().addFlow(packetV5);
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

    private NetflowDevice createTestDevice(String deviceIpAddress) {
        SecurityUserEntity securityUser = securityUserRepository.findByUsername("sample example emp")
                .orElseThrow(UserNotExistException::new);
        //NacRoleEntity nacRoleEntity = nacRoleRepository.findByName("EXAMPLE_EMPLOYEE").orElse(null);
        String mac = randomMACAddress();
        UserDeviceEntity userDeviceEntity = new UserDeviceEntity(mac,
                "test device",
                UserBlockInfoEntity.builder().isBlocked(false).build(),
                securityUser,
                deviceIpAddress,
                Collections.emptySet());
        userDeviceEntity.getBlackListInfo().setUserDeviceEntity(userDeviceEntity);
        netflowDeviceList.addDevice(userDeviceEntity);
        ssh.permitDevicePorts(deviceIpAddress, securityUser.getPorts());
        log.info("Created TEST DEVICE: " + userDeviceEntity);
        return netflowDeviceList.getUserByMacAddress(mac);
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
