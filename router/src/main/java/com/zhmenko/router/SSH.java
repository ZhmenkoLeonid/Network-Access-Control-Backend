package com.zhmenko.router;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;

public abstract class SSH {
    protected final int port = 22;
    protected String user;
    protected String password;
    protected String host;
    protected JSch jsch;
    protected Session session;
    protected Channel channel;
    protected InputStream in;
    protected OutputStream out;
    protected String accessListName;

    public SSH(String usr, String pass, String hostIP,String accessListName) throws JSchException {
        this.user = usr;
        this.password = pass;
        this.host = hostIP;
        this.accessListName = accessListName;
        this.jsch = new JSch();
        this.session = jsch.getSession(user, host, port);
        this.session.setPassword(pass);
        this.session.setConfig("StrictHostKeyChecking", "no");
    }

    public abstract String sendCommand(String command, boolean isReqEnableMode,boolean isReqConfigMode);

    public abstract void permitUser(String ipAddress);

    public abstract void permitUserPort(String ipAddress, int port);

    public abstract void permitUserPorts(String ipAddress, List<Integer> ports);

    public abstract void denyUserPort(String ipAddress, int port);

    public abstract void denyUserPorts(String ipAddress, List<Integer> ports);

    public abstract void denyUser(String ipAddress);

    protected void establishConnection() {
        try {
            session.connect();
            channel = session.openChannel("shell");
            channel.setOutputStream(System.out);
            channel.setInputStream(System.in);
            in = channel.getInputStream();
            out = channel.getOutputStream();
            channel.connect();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void close() throws IOException {
        if (channel.isConnected()) {
            channel.disconnect();
            in.close();
            out.close();
        }
        if (session.isConnected())
            session.disconnect();
    }

    protected void write(String cmd) {
        try {
            out.write(cmd.getBytes());
            out.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
