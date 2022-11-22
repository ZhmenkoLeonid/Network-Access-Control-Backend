package com.zhmenko.data.netflow.models.exception;

public class UserNotExistException extends RuntimeException {
    public UserNotExistException(String message) {
        super(message);
    }

    public UserNotExistException() {super();}
}
