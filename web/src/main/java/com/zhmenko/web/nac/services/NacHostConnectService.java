package com.zhmenko.web.nac.services;

import com.zhmenko.hostvalidation.host.ValidationPacket;

public interface NacHostConnectService {
    boolean connect(ValidationPacket data);

    boolean postConnect(ValidationPacket data);
}
