package com.zhmenko.ips.router_interaction;


// access-list 1 permit...
//int g0/0 -> ip access-group 1 in

import com.jcraft.jsch.JSchException;

import java.io.IOException;

public class SSHCisco extends SSH{
    private boolean isEnableMode;
    private boolean isConfigMode;
    private String enableModePassword;
    public SSHCisco(String usr, String pass, String hostIP, String accessListName, String enableModePassword)
            throws InterruptedException, JSchException, IOException {
        super(usr, pass, hostIP,accessListName);
        isEnableMode = false;
        isConfigMode = false;
        this.enableModePassword = enableModePassword;
    }
    public SSHCisco() throws InterruptedException, JSchException, IOException {
        super();
        isEnableMode = false;
        isConfigMode = false;
        this.enableModePassword = "cisco";
    }

    public String sendCommand(String command, boolean isReqEnableMode,boolean isReqConfigMode)
            throws InterruptedException, JSchException, IOException {
        if (!session.isConnected()) { establishConnection(); }

        if (isReqEnableMode) { enterEnableMode();}
        else if (isReqConfigMode) {enterConfigMode();}

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
        enterConfigMode();
        out.write(("access-list "+accessListName+" permit host "+ipAddress+'\n').getBytes());
        out.flush();
    }

    @Override
    public void denyUser(String ipAddress) throws InterruptedException, JSchException, IOException {
        if (!session.isConnected()) { establishConnection(); }
        enterConfigMode();
        out.write(("access-list "+accessListName+" deny host "+ipAddress+'\n').getBytes());
        out.flush();
    }
    private void enterEnableMode() throws IOException {
        if (isEnableMode) return;
        if (isConfigMode) {
            out.write("end\n".getBytes());
            out.flush();
            isConfigMode = false;
            isEnableMode = true;
            return;
        }
        out.write("enable\n".getBytes());
        out.flush();
        if (!enableModePassword.equals("")){
            out.write((enableModePassword +'\n').getBytes());
            out.flush();
        }
        isEnableMode = true;
    }
    private void enterConfigMode() throws IOException {
        if (isConfigMode) return;
        if (isEnableMode) {
            out.write("configure terminal\n".getBytes());
            out.flush();
            isEnableMode = false;
            isConfigMode = true;
            return;
        }
        enterEnableMode();
        enterConfigMode();
    }
}
