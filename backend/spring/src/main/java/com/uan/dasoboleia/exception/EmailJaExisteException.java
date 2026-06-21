package com.uan.dasoboleia.exception;

public class EmailJaExisteException extends RuntimeException{
    
    public EmailJaExisteException(String email) {
        super("Já existe um Utente registado com o email: "+ email);
    }
}
