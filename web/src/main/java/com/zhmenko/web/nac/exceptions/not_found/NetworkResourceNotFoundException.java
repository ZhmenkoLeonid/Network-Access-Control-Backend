package com.zhmenko.web.nac.exceptions.not_found;

public class NetworkResourceNotFoundException extends NotFoundException {
    public NetworkResourceNotFoundException(int port) {
        super("Ресурс с портом \"" + port + "\" не найден!");
    }
}
