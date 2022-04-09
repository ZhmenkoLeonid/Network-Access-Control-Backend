package com.zhmenko.ips.router_interaction;

import com.jcraft.jsch.JSchException;

import java.io.*;

public class SSHKeenetic extends SSH{
    //String remoteFile = "/home/john/test.txt";
    public SSHKeenetic(String usr, String pass, String hostIP,String accessListName) throws InterruptedException,
            IOException, JSchException {
        super(usr, pass, hostIP,accessListName);
    }

    public SSHKeenetic() throws IOException, InterruptedException, JSchException {
        super();
    }

// access-list 1 deny ip <ip_address> 255.255.255.255 0.0.0.0 0.0.0.0

//   access-list 1 permit ip <ip_address> 255.255.255.255 0.0.0.0 0.0.0.0
//   no access-list 1 permit ip <ip_address> 255.255.255.255 0.0.0.0 0.0.0.0

    // это чтоб поставить "запрет на всё" в конец списка |
    //                                                   v
//   no access-list 1 deny ip 192.168.1.0 255.255.255.0 0.0.0.0 0.0.0.0
//   access-list 1 deny ip 192.168.1.0 255.255.255.0 0.0.0.0 0.0.0.0
    @Override
    public String sendCommand(String command) throws JSchException, IOException, InterruptedException {
        if (!session.isConnected()) { establishConnection(); }
        out.write((command + '\n').getBytes());
        out.flush();
        String result = "";
        byte[] answer = new byte[1024];
        Thread.sleep(1000);
        while (in.available() > 0) {
            int i = in.read(answer, 0, 1024);
            result += new String(answer, 0, i);
            // if (i < 0) break;
        }
        return result;
    }

    @Override
    public void permitUser(String ipAddress) throws InterruptedException, JSchException, IOException {
        if (!session.isConnected()) { establishConnection(); }
        out.write(("access-list "+accessListName+" permit ip " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0" + '\n').getBytes());
        out.flush();
        // от беды подальше пока закомменчу
        //addDenyRuleToEndList();
        // TODO опасное место, закомментить от беды подальше!!!!
    }
    @Override
    public void denyUser(String ipAddress) throws InterruptedException, JSchException, IOException {
        if (!session.isConnected()) { establishConnection(); }
        out.write(("no access-list "+accessListName+" permit ip " + ipAddress +
                " 255.255.255.255 0.0.0.0 0.0.0.0" + '\n').getBytes());
        out.flush();
    }

    private void addDenyRuleToEndList() throws InterruptedException, JSchException, IOException {
        if (!session.isConnected()) { establishConnection(); }
        out.write(("no access-list "+accessListName+
                " deny ip 192.168.1.0 255.255.255.0 0.0.0.0 0.0.0.0" + '\n').getBytes());
        out.flush();
        out.write(("access-list "+accessListName+
                " deny ip 192.168.1.0 255.255.255.0 0.0.0.0 0.0.0.0"+'\n').getBytes());
        out.flush();
    }
}

