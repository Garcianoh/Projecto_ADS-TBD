package com.uan.dasoboleia.exception;

public class UtenteNaoEncontradoException extends RuntimeException {
    
    public UtenteNaoEncontradoException(Long id) {
        super("Utente não encontrado com o id: " + id);
    }
}
