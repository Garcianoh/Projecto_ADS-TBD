package com.uan.dasoboleia.exception;

public class NickJaExisteException extends RuntimeException {
    
    public NickJaExisteException(String nick) {
        super("Já existe um utente registado com o pseudónimo: " + nick);
    }
}
