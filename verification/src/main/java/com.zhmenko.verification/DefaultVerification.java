package com.zhmenko.verification;

import com.zhmenko.model.host.HostData;
import org.springframework.stereotype.Component;

@Component
public class DefaultVerification implements Verification{
    @Override
    public boolean check(HostData data) {
        String macAddress = data.getMacAddress();
        String ipAddress = data.getIpAddress();

        return (macAddress != null
                && ipAddress != null
                && macAddress.length() == 17
                && ipAddress.length() >= 7
                && ipAddress.length() <= 15);
    }
}
