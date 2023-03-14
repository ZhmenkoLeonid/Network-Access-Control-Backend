package com.zhmenko.data.netflow.models.exception;

import java.util.UUID;

public class UserNotExistException extends RuntimeException {
    public UserNotExistException(String message) {
        super(message);
    }

    public UserNotExistException() {super("Пользователь не существует");}

    public UserNotExistException(UUID userId) {
        super(String.format("Пользователь с id %s не существует", userId.toString()));
    }
}
