package com.zhmenko.router;


// access-list 1 permit...
//int g0/0 -> ip access-group 1 in

import com.jcraft.jsch.JSchException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


@Component
@Profile("CISCO")
public class SSHCisco extends SSH {
    private boolean isEnableMode;
    private boolean isConfigMode;
    private String enableModePassword;

    public SSHCisco(String usr, String pass, String hostIP, String accessListName, String enableModePassword)
            throws InterruptedException, JSchException, IOException {
        super(usr, pass, hostIP, accessListName);
        this.enableModePassword = enableModePassword;
    }

    public SSHCisco() throws InterruptedException, JSchException, IOException {
        super();
        this.enableModePassword = "cisco";
    }

    public String sendCommand(String command, boolean isReqEnableMode, boolean isReqConfigMode) {
        if (!session.isConnected()) {
            establishConnection();
        }

        if (isReqEnableMode) {
            enterEnableMode();
        } else if (isReqConfigMode) {
            enterConfigMode();
        }

        write(command + '\n');
        String result = "";

        try {
            byte[] answer = new byte[1024];
            Thread.sleep(1000);
            while (in.available() > 0) {
                int i = in.read(answer, 0, 1024);
                result += new String(answer, 0, i);
                // if (i < 0) break;
            }
        } catch (Exception e) {
            result = "Internal Error";
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void permitUser(String ipAddress) {
        if (!session.isConnected()) {
            establishConnection();
        }
        enterConfigMode();
        write("access-list " + accessListName + " permit host " + ipAddress + '\n');
    }

    @Override
    public void permitUserPort(String ipAddress, int port) {
        throw new AssertionError();
    }

    @Override
    public void permitUserPorts(String ipAddress, List<Integer> ports) {
        throw new AssertionError();
    }

    @Override
    public void denyUser(String ipAddress) {
        if (!session.isConnected()) {
            establishConnection();
        }
        enterConfigMode();
        write("access-list " + accessListName + " deny host " + ipAddress + '\n');
    }

    @Override
    public void denyUserPort(String ipAddress, int port) {
        throw new AssertionError();
    }

    @Override
    public void denyUserPorts(String ipAddress, List<Integer> ports) {
        throw new AssertionError();
    }

    private void enterEnableMode() {
        if (isEnableMode) return;
        if (isConfigMode) {
            write("end\n");
            isConfigMode = false;
            isEnableMode = true;
            return;
        }
        write("enable\n");
        if (!enableModePassword.equals("")) {
            write(enableModePassword + '\n');
        }
        isEnableMode = true;
    }

    private void enterConfigMode() {
        if (isConfigMode) return;
        if (isEnableMode) {
            write("configure terminal\n");
            isEnableMode = false;
            isConfigMode = true;
            return;
        }
        enterEnableMode();
        enterConfigMode();
    }
}
