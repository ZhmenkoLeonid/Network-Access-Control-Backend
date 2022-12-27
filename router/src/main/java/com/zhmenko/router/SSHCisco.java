package com.zhmenko.router;


// access-list 1 permit...
//int g0/0 -> ip access-group 1 in

import com.jcraft.jsch.JSchException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Qualifier("cisco")
public class SSHCisco extends SSH {
    private boolean isEnableMode;
    private boolean isConfigMode;
    private final String enableModePassword;

    public SSHCisco(SSHProperties sshProperties)
            throws JSchException {
        super(sshProperties.getUsername(),
                sshProperties.getPassword(),
                sshProperties.getIpAddress(),
                sshProperties.getAccessListName());
        this.enableModePassword = sshProperties.getEnabledModePassword();
    }

    public String sendCommand(String command, boolean isReqEnableMode, boolean isReqConfigMode) {
        if (isReqEnableMode) {
            enterEnableMode();
        } else if (isReqConfigMode) {
            enterConfigMode();
        }

        write(command + '\n');
        StringBuilder result = new StringBuilder();

        try {
            byte[] answer = new byte[1024];
            Thread.sleep(1000);
            while (in.available() > 0) {
                int i = in.read(answer, 0, 1024);
                result.append(new String(answer, 0, i));
                // if (i < 0) break;
            }
        } catch (Exception e) {
            result = new StringBuilder("Internal Error");
            e.printStackTrace();
        }
        return result.toString();
    }

    @Override
    public void permitUser(String ipAddress) {
        enterConfigMode();
        write("access-list " + accessListName + " permit host " + ipAddress + '\n');
    }

    @Override
    public String permitUserPort(String ipAddress, int port) {
        throw new AssertionError();
    }

    @Override
    public String permitDevicePorts(String ipAddress, Set<Integer> ports) {
        throw new AssertionError();
    }

    @Override
    public void denyUser(String ipAddress) {
        enterConfigMode();
        write("access-list " + accessListName + " deny host " + ipAddress + '\n');
    }

    @Override
    public void denyUserPort(String ipAddress, int port) {
        throw new AssertionError();
    }

    @Override
    public String denyDevicePorts(String ipAddress, Iterable<Integer> ports) {
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
