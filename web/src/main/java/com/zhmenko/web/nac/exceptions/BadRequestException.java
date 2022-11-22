package com.zhmenko.web.nac.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
    public BadRequestException() {super("Ошибка при валидации данных");}
}
