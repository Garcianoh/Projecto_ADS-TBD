package com.uan.dasoboleia.exception;

public class ContaBloqueadaException extends RuntimeException {
    
    public ContaBloqueadaException(int minutosRestantes) {
        super("Conta temporariamente bloqueada devido a várias tentativas falhadas. "
            + "Tente novamente em " + minutosRestantes + " minutos(s).");
    }
}
