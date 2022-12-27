package com.zhmenko.router;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.List;
import java.util.Set;

public abstract class SSH {
    protected final int port = 22;
    protected String user;
    protected String password;
    protected String host;
    protected JSch jsch;
    protected Session session;
    protected ChannelExec channel;
    ChannelShell channelShell;
    protected InputStream in;
    protected OutputStream out;
    protected String accessListName;

    public SSH(String usr, String pass, String hostIP, String accessListName) throws JSchException {
        this.user = usr;
        this.password = pass;
        this.host = hostIP;
        this.accessListName = accessListName;
        this.jsch = new JSch();
    }

    public String sendCommand(String command, boolean isReqEnableMode, boolean isReqConfigMode) {
       return connectAndExecuteCommand(command);
    }


    public String sendCommands(List<String> commands, boolean isReqEnableMode, boolean isReqConfigMode) {
        throw new AssertionError();
    }

    public abstract void permitUser(String ipAddress);

    public abstract String permitUserPort(String ipAddress, int port);

    public abstract String permitDevicePorts(String ipAddress, Set<Integer> ports);

    public String permitDevicesPorts(Iterable<String> ipAddresses, Set<Integer> ports) {
        throw new AssertionError();
    }

    public abstract void denyUserPort(String ipAddress, int port);

    public abstract String denyDevicePorts(String ipAddress, Iterable<Integer> ports);

    public String denyDevicesPorts(Iterable<String> ipAddresses, Iterable<Integer> ports) {
        throw new AssertionError();
    }

    public String updateDevicesPorts(Iterable<String> ipAddresses, Set<Integer> oldPorts, Set<Integer> newPorts) {
        throw new AssertionError();
    }

    public abstract void denyUser(String ipAddress);

    public String denyUsers(List<String> ipAddresses) {
        throw new AssertionError();
    }

/*    protected void establishConnection() {
        try {
            session = jsch.getSession(user, host, port);
            this.session.setPassword(password);
            this.session.setConfig("StrictHostKeyChecking", "no");
            session.connect(3000);

            while (!session.isConnected()) {
                Thread.sleep(100);
            }

            channel = session.openChannel("shell");
            channel.setOutputStream(System.out);
            channel.setInputStream(System.in);
            in = channel.getInputStream();
            out = channel.getOutputStream();
            channel.connect(3000);

            byte[] answer = new byte[1024];
            long acc = 0;
            while (in.available() == 0) {
                System.out.println("wait response for establishing connection action");
                Thread.sleep(100);
                acc+=100;
                if (acc > 1000) {
                    System.out.println("Response wait timeout. Try again!");
                    establishConnection();
                }
            }
            while (in.available() > 0) {
                in.read(answer, 0, 1024);
            }
            System.out.println("establish connection response read successfully");
        } catch (Exception e) {
            e.printStackTrace();
            establishConnection();
        }
    }*/

/*    private void openChannel() throws JSchException, IOException {
        if (!session.isConnected()) {
            establishConnection();
            return;
        }

        channel = session.openChannel("shell");
        channel.setOutputStream(System.out);
        channel.setInputStream(System.in);
        in = channel.getInputStream();
        out = channel.getOutputStream();
        channel.connect(3000);
    }*/



    public void close() throws IOException {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
            in.close();
            out.close();
        }
        if (session != null && session.isConnected())
            session.disconnect();
    }

    synchronized public String connectAndExecuteCommand(String cmd) {
        //List<String> lines = new ArrayList<String>();
        System.out.println("sending command:\n" +cmd);
        String dataFromChannel = "No response";
        try {
            openSession();
            initChannel(cmd, session);
            InputStream in = channel.getInputStream();
            channel.connect();
            dataFromChannel = getDataFromChannel(channel, in);
            //lines.addAll(Arrays.asList(dataFromChannel.split("\n")));
            channel.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("response:\n" + dataFromChannel);
        return dataFromChannel;
    }

    private String getDataFromChannel(Channel channel, InputStream in)
            throws IOException {
        String exitStatusMsg;
        StringBuilder result = new StringBuilder();
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                result.append(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                int exitStatus = channel.getExitStatus();
                exitStatusMsg = "exit-status: " + exitStatus;
                break;
            }
            trySleep(100);
        }
        return exitStatusMsg + "\n" + result;
    }

    private void openSession() throws JSchException {
        if (session == null || !session.isConnected()) {
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            UserInfo userInfo = new MyUserInfo();
            session.setUserInfo(userInfo);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(3000);
        }
    }

    private Channel initChannel(String commands, Session session) throws JSchException {
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(commands.getBytes());
        channel.setInputStream(null);
        channel.setErrStream(System.err);
        return channel;
    }

    private void trySleep(int sleepTimeMillis) {
        try {
            Thread.sleep(sleepTimeMillis);
        } catch (Exception e) {
        }
    }
    synchronized protected void write(String cmd) {
        try {
            System.out.println("sending command:\n" + cmd);
            if (session == null || !session.isConnected()) openSession();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(cmd);
            out = new ByteArrayOutputStream();
            channel.setOutputStream(out);
            channel.setInputStream(System.in);
            in = channel.getInputStream();
            channel.connect(3000);

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            channel.disconnect();
/*            if (session == null || channel == null ||
                    !session.isConnected() || !channel.isConnected()) {
                System.out.println("establish connection");
                //close();
                establishConnection();
            } else openChannel();
            out.write(cmd.getBytes());
            out.flush();*/
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("try send command again");
            write(cmd);
        }
    }
}
