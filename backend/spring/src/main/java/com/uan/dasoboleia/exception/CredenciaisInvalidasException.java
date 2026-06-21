package com.uan.dasoboleia.exception;

public class CredenciaisInvalidasException extends RuntimeException {
    
    public CredenciaisInvalidasException() {
        super("Email ou password incorretos");
    }
}
