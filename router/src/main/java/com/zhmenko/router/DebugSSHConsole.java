package com.zhmenko.router;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.zhmenko.router.socket.ReadThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Deprecated()
public class DebugSSHConsole {
    private static final int port = 22;
    private static String user = "admin";
    private static String password = "123456";
    private static String host = "192.168.1.1";
    private static JSch jsch = new JSch();
    private static Session session;
    private static Channel channel;
    private static InputStream in;
    private static OutputStream out;

    public static void start() throws JSchException, IOException {
        session = jsch.getSession(user, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        System.out.println("Establishing Connection...");
        session.connect();
        System.out.println("Connection established.");
        channel = session.openChannel("shell");
        channel.setOutputStream(System.out);
        channel.setInputStream(System.in);
        in = channel.getInputStream();
        out = channel.getOutputStream();
        channel.connect();
        ReadThread thread = new ReadThread(in,channel);
        thread.start();
    }
}
