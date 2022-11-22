package com.zhmenko.router;

import com.jcraft.jsch.JSchException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
    @Override
    public String sendCommand(String command, boolean isReqEnableMode, boolean isReqConfigMode) {
        StringBuilder result = new StringBuilder();

        if (!session.isConnected()) {
            establishConnection();
        }
        write(command + '\n');

        try {
            byte[] answer = new byte[1024];
            Thread.sleep(1000);
            while (in.available() > 0) {
                int i = in.read(answer, 0, 1024);
                result.append(new String(answer, 0, i));
            }
        } catch (InterruptedException | IOException e) {
            result = new StringBuilder("Error");
            e.printStackTrace();
        }
        return result.toString();
    }

    @Override
    public void permitUser(String ipAddress) {
        Objects.requireNonNull(ipAddress);

        if (!session.isConnected()) {
            establishConnection();
        }
        write("access-list " + accessListName + " permit ip " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0" + '\n');
        addDenyRuleToEndList();
    }

    @Override
    public void permitUserPort(String ipAddress, int port) {
        if (!session.isConnected()) {
            establishConnection();
        }
        write("access-list " + accessListName + " permit tcp " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0 port eq " + port);
        addDenyRuleToEndList();
    }

    @Override
    public void permitUserPorts(String ipAddress, List<Integer> ports) {
        Objects.requireNonNull(ipAddress);
        Objects.requireNonNull(ports);
        if (ports.size() == 0) return;

        if (!session.isConnected()) {
            establishConnection();
        }

        String template = "access-list " + accessListName + " permit tcp " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0 port eq ";

        StringBuilder command = new StringBuilder();

        for (Integer port : ports) {
            command.append(template)
                    .append(port)
                    .append("\n");
        }

        write(command.toString());
        addDenyRuleToEndList();
    }

    @Override
    public void denyUser(String ipAddress) {
        Objects.requireNonNull(ipAddress);
        if (!session.isConnected()) {
            establishConnection();
        }
        write("no access-list " + accessListName + " permit ip " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0" + '\n');
    }

    @Override
    public void denyUserPort(String ipAddress, int port) {
        Objects.requireNonNull(ipAddress);
        if (!session.isConnected()) {
            establishConnection();
        }
        write("no access-list " + accessListName + " permit tcp " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0 port eq " + port);
    }

    @Override
    public void denyUserPorts(String ipAddress, List<Integer> ports) {
        Objects.requireNonNull(ipAddress);
        Objects.requireNonNull(ports);
        if (ports.size() == 0) return;

        if (!session.isConnected()) {
            establishConnection();
        }

        String template = "no access-list " + accessListName + " permit tcp " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0 port eq ";

        StringBuilder command = new StringBuilder();

        for (Integer port : ports) {
            command.append(template)
                    .append(port)
                    .append("\n");
        }

        write(command.toString());
    }

    private void addDenyRuleToEndList() {
        if (!session.isConnected()) {
            establishConnection();
        }
        write("no access-list " + accessListName +
                " deny ip 0.0.0.0 0.0.0.0 0.0.0.0 0.0.0.0" + '\n');
        write("access-list " + accessListName +
                " deny ip 0.0.0.0 0.0.0.0 0.0.0.0 0.0.0.0" + '\n');
    }
}

