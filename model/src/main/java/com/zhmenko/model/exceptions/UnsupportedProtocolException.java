package com.zhmenko.model.exceptions;

public class UnsupportedProtocolException extends Exception {
    private String protocolType;
    public UnsupportedProtocolException(String protocolType){
        this.protocolType = protocolType;
    }
    @Override
    public void printStackTrace() {
        System.err.print("Следующий идентификатор типа протокола не поддерживается: "+protocolType);
    }
}
