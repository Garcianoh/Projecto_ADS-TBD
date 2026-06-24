package com.uan.dasoboleia.exception;

public class CodigoInvalidoException extends RuntimeException {

    public CodigoInvalidoException() {
        super("Codigo inválido ou expirado.");
    }
}
