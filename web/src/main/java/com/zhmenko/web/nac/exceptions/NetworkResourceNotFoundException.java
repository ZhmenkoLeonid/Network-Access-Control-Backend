package com.zhmenko.web.nac.exceptions;

public class NetworkResourceNotFoundException extends RuntimeException {
    public NetworkResourceNotFoundException(int port) {
        super("Ресурс с портом \"" + port + "\" не найден!");
    }
}
