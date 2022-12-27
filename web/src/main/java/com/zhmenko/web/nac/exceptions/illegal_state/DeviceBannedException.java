package com.zhmenko.web.nac.exceptions.illegal_state;

public class DeviceBannedException extends IllegalStateException {
    public DeviceBannedException(String msg) {
        super(msg);
    }
}
