package com.zhmenko.web.nac.exceptions.illegal_state;

public class IllegalStateException extends RuntimeException {
    public IllegalStateException() {
        super();
    }

    public IllegalStateException(String message) {
        super(message);
    }

    public IllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalStateException(Throwable cause) {
        super(cause);
    }

    protected IllegalStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
