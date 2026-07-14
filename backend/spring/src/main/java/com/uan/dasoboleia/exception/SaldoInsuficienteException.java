package com.uan.dasoboleia.exception;

public class SaldoInsuficienteException extends RuntimeException {
    
    public SaldoInsuficienteException() {
        super("Saldo insuficiente para realizar esta operação.");
    }
}
