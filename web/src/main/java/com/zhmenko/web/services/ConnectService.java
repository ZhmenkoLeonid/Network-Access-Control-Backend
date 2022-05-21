package com.zhmenko.web.services;

import com.zhmenko.hostvalidation.host.ValidationPacket;

public interface ConnectService {
    boolean connect(ValidationPacket data, String ipAddress);

    boolean postConnect(ValidationPacket data, String ipAddress);
}
