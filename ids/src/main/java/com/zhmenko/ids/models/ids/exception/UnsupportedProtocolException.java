package com.zhmenko.ids.models.ids.exception;

public class UnsupportedProtocolException extends RuntimeException {
    private final String protocolType;
    public UnsupportedProtocolException(String protocolType){
        this.protocolType = protocolType;
    }
    @Override
    public void printStackTrace() {
        System.err.print("Следующий идентификатор типа протокола не поддерживается: " + protocolType);
    }
}
