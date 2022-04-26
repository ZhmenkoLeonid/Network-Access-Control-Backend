package com.zhmenko.router;

import com.jcraft.jsch.JSchException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

@Component
@Profile("KEENETIC")
public class SSHKeenetic extends SSH {
    //String remoteFile = "/home/john/test.txt";
    public SSHKeenetic(String usr, String pass, String hostIP, String accessListName) throws InterruptedException,
            IOException, JSchException {
        super(usr, pass, hostIP, accessListName);
    }

    public SSHKeenetic() throws IOException, InterruptedException, JSchException {
        super();
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
        String result = "";

        if (!session.isConnected()) {
            establishConnection();
        }
        write(command + '\n');

        try {
            byte[] answer = new byte[1024];
            Thread.sleep(1000);
            while (in.available() > 0) {
                int i = in.read(answer, 0, 1024);
                result += new String(answer, 0, i);
            }
        } catch (InterruptedException | IOException e) {
            result = "Error";
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void permitUser(String ipAddress) {
        if (!session.isConnected()) {
            establishConnection();
        }
        write("access-list " + accessListName + " permit ip " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0" + '\n');
        // от беды подальше пока закомменчу
        addDenyRuleToEndList();
        // TODO опасное место, закомментить от беды подальше!!!!
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
        if (!session.isConnected()) {
            establishConnection();
        }
        write("no access-list " + accessListName + " permit ip " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0" + '\n');
    }

    @Override
    public void denyUserPort(String ipAddress, int port) {
        if (!session.isConnected()) {
            establishConnection();
        }
        write("no access-list " + accessListName + " permit tcp " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0 port eq " + port);
    }

    @Override
    public void denyUserPorts(String ipAddress, List<Integer> ports) {
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

