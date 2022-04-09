package com.zhmenko.ips.router_interaction;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public SSH(String usr, String pass, String hostIP,String accessListName) throws InterruptedException,
            IOException, JSchException {
        user = usr;
        password = pass;
        host = hostIP;
        this.accessListName = accessListName;
        jsch = new JSch();
        session = jsch.getSession(user, host, port);
        session.setPassword(pass);
        session.setConfig("StrictHostKeyChecking", "no");
    }

    public SSH() throws IOException, InterruptedException, JSchException {
        user = "admin";
        password = "135790";
        host = "192.168.1.1";
        this.accessListName = "1";
        jsch = new JSch();
        session = jsch.getSession(user, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
    }

    public String sendCommand(String command) throws JSchException, IOException, InterruptedException {
        return "";
    }

    public void permitUser(String ipAddress) throws InterruptedException, JSchException, IOException {

    }

    public void denyUser(String ipAddress) throws InterruptedException, JSchException, IOException {

    }

    void establishConnection() throws JSchException, IOException, InterruptedException {
        session.connect();
        channel = session.openChannel("shell");
        channel.setOutputStream(System.out);
        channel.setInputStream(System.in);
        in = channel.getInputStream();
        out = channel.getOutputStream();
        channel.connect();
        Thread.sleep(1000);
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

}
