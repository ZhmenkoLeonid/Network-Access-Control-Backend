package com.zhmenko.hostvalidation;

import com.zhmenko.hostvalidation.host.ValidationPacket;

public interface HostValidation {
    public boolean check(ValidationPacket data);
}
