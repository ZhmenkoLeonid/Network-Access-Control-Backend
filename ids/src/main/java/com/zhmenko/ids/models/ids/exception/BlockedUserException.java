package com.zhmenko.ids.models.ids.exception;

public class BlockedUserException extends RuntimeException {
    private final String ipAddress;
    public BlockedUserException(String ipAddress){
        this.ipAddress = ipAddress;
    }
    @Override
    public void printStackTrace() {
        System.err.print("Следующий пользователь находится в чёрном списке: "+ipAddress);
    }
}
