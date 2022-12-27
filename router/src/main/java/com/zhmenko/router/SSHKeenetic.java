package com.zhmenko.router;

import com.jcraft.jsch.JSchException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Qualifier("keenetic")
@Primary
public class SSHKeenetic extends SSH {
    public SSHKeenetic(SSHProperties sshProperties) throws JSchException {
        super(sshProperties.getUsername(),
                sshProperties.getPassword(),
                sshProperties.getIpAddress(),
                sshProperties.getAccessListName());
    }

    // permit tcp 0.0.0.0 0.0.0.0 0.0.0.0 0.0.0.0 port eq 443

// access-list 1 deny ip <ip_address> 255.255.255.255 0.0.0.0 0.0.0.0

//   access-list 1 permit ip <ip_address> 255.255.255.255 0.0.0.0 0.0.0.0
//   no access-list 1 permit ip <ip_address> 255.255.255.255 0.0.0.0 0.0.0.0

    // это чтоб поставить "запрет на всё" в конец списка |
    //                                                   v
//   no access-list 1 deny ip 192.168.1.0 255.255.255.0 0.0.0.0 0.0.0.0
//   access-list 1 deny ip 192.168.1.0 255.255.255.0 0.0.0.0 0.0.0.0
    public String sendCommands(List<String> commands, boolean isReqEnableMode, boolean isReqConfigMode) {
        if (commands.size() == 0) return "empty command list";

        StringJoiner result = new StringJoiner("\n");

        for (String command : commands) {
            result.add(connectAndExecuteCommand(command));
        }
/*        ///StringBuilder tempBuilder = new StringBuilder(commands.get(0));
        StringJoiner stringJoiner = new StringJoiner("\\n");
        stringJoiner.add(commands.get(0));
        for (int i = 1; i < commands.size(); i++) {
            stringJoiner.add(commands.get(i));
            if (i % (batchSize - 1) == 0) {
                result.add("batch " + batchNum++);

                String batchResponse = sendCommand(stringJoiner.toString(), isReqEnableMode, isReqConfigMode);
                if (batchResponse.equals("Error")) {
                    throw new IllegalStateException("Router not run commands!");
                } else result.add(batchResponse);

                stringJoiner = new StringJoiner("\\n");
            }
        }
        if (stringJoiner.length() > 0) {
            System.out.println("last command: \"" + stringJoiner.toString() + "\"");
            result.add("batch " + batchNum);
            String batchResponse = sendCommand(stringJoiner.toString(), isReqEnableMode, isReqConfigMode);
            if (batchResponse.equals("Error")) {
                throw new IllegalStateException("Router not runned commands!");
            } else result.add(batchResponse);
        }*/
        return result.toString();
    }

    @Override
    public void permitUser(String ipAddress) {
        Objects.requireNonNull(ipAddress);

        write("access-list " + accessListName + " permit ip " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0" + '\n');
        addDenyRuleToEndList();
    }

    @Override
    public String permitUserPort(String ipAddress, int port) {
        Objects.requireNonNull(ipAddress);

        String response = connectAndExecuteCommand("access-list " + accessListName + " permit tcp " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0 port eq " + port);
        addDenyRuleToEndList();
        return response;
    }

    @Override
    public String permitDevicePorts(String ipAddress, Set<Integer> ports) {
        Objects.requireNonNull(ipAddress);
        Objects.requireNonNull(ports);
        if (ports.size() == 0) return "empty ports list";

        String template = "access-list " + accessListName + " permit tcp " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0 port eq ";

        List<String> commands = new ArrayList<>();

        for (Integer port : ports) {
            commands.add(template + port);
        }

        String response = sendCommands(commands, false, false);
        addDenyRuleToEndList();
        return response;
    }

    @Override
    public String permitDevicesPorts(Iterable<String> ipAddresses, Set<Integer> ports) {
        Objects.requireNonNull(ipAddresses);
        Objects.requireNonNull(ports);
        if (ports.size() == 0 || !ipAddresses.iterator().hasNext()) return "empty addresses or ports list";

        List<String> batchCommand = new ArrayList<>();

        // add ports for every ip from ipAddresses list
        for (String ipAddress : ipAddresses) {
            String template = "access-list " + accessListName + " permit tcp " + ipAddress +
                    " 255.255.255.255 0.0.0.0 0.0.0.0 port eq ";

            for (Integer port : ports) {
                batchCommand.add(template + port);
            }
        }

        //String response = sendCommand(batchCommand.toString(), false, false);
        String response = sendCommands(batchCommand, false, false);
        addDenyRuleToEndList();
        return response;
    }

    @Override
    @Deprecated(since = "Not working")
    public void denyUser(String ipAddress) {
        Objects.requireNonNull(ipAddress);

        write("no access-list " + accessListName + " permit ip " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0" + '\n');
    }

    @Override
    @Deprecated(since = "Not working")
    public String denyUsers(List<String> ipAddresses) {
        Objects.requireNonNull(ipAddresses);
        if (ipAddresses.size() == 0) return "empty ip list";

        StringBuilder batchCommand = new StringBuilder();
        for (String ipAddress : ipAddresses) {
            batchCommand.append("no access-list " + accessListName + " permit ip " + ipAddress +
                            " 255.255.255.255 0.0.0.0 0.0.0.0")
                    .append('\n');
        }
        write(batchCommand.toString());
        return "success deny users";
    }

    @Override
    @Deprecated
    public void denyUserPort(String ipAddress, int port) {
        Objects.requireNonNull(ipAddress);

        write("no access-list " + accessListName + " permit tcp " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0 port eq " + port);
    }

    @Override
    public String denyDevicePorts(String ipAddress, Iterable<Integer> ports) {
        Objects.requireNonNull(ipAddress);
        Objects.requireNonNull(ports);
        if (!ports.iterator().hasNext()) return "empty ports list";

        String template = "no access-list " + accessListName + " permit tcp " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0 port eq ";

        List<String> commands = new ArrayList<>();

        for (Integer port : ports) {
            commands.add(template + port);
        }

        return sendCommands(commands, false, false);
    }

    @Override
    public String denyDevicesPorts(Iterable<String> ipAddresses, Iterable<Integer> ports) {
        Objects.requireNonNull(ipAddresses);
        Objects.requireNonNull(ports);
        if (!ports.iterator().hasNext() || !ipAddresses.iterator().hasNext())
            return "empty ports or ip addresses list";

        List<String> batchCommand = new ArrayList<>();

        for (String ipAddress : ipAddresses) {
            String template = "no access-list " + accessListName + " permit tcp " + ipAddress +
                    " 255.255.255.255 0.0.0.0 0.0.0.0 port eq ";

            for (Integer port : ports) {
                batchCommand.add(template + port);
            }
        }
        return sendCommands(batchCommand, false, false);
    }

    /**
     * Устанавливает в acl роутера порты из множества newPorts для указанных ip-адресов,
     * при этом удаляя порты из множества oldPorts, если их нет в множестве newPorts
     * @param ipAddresses - список адресов, к которым будут применяться правила
     * @param oldPorts - список портов, которые будут удалены из acl, если они не содержаться в множестве newPorts
     * @param newPorts - список портов, которые будут содержаться в acl в результате вызова метода
     * @return - лог роутера или сообщение в случае, если в итоге обращения к роутеру не было
     */
    @Override
    public String updateDevicesPorts(Iterable<String> ipAddresses, Set<Integer> oldPorts, Set<Integer> newPorts) {
        Set<Integer> oldPortsLocal = new HashSet<>(oldPorts);
        Set<Integer> newPortsLocal = new HashSet<>(newPorts);
        Set<Integer> oldPortsLocalTemp = new HashSet<>(oldPorts);

        oldPortsLocal.removeAll(newPortsLocal);
        newPortsLocal.removeAll(oldPortsLocalTemp);

        // Удаляем старые порты
        String denyPortsLog = "deny ports response: \n" + denyDevicesPorts(ipAddresses, oldPortsLocal);
        // Добавляем новые порты
        String permitPortsLog = "permit ports response: \n" + permitDevicesPorts(ipAddresses, newPortsLocal);

        return denyPortsLog + "\n" + permitPortsLog;
    }

    private void addDenyRuleToEndList() {
        sendCommands(List.of
                (
                "no access-list " + accessListName + " deny ip 0.0.0.0 0.0.0.0 0.0.0.0 0.0.0.0",
                "access-list " + accessListName + " deny ip 0.0.0.0 0.0.0.0 0.0.0.0 0.0.0.0"
                ), false, false);
    }
}

