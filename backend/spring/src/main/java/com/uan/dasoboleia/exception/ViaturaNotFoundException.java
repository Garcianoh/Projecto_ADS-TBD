package com.uan.dasoboleia.exception;

public class ViaturaNotFoundException extends RuntimeException {
    
    public ViaturaNotFoundException(Long id) {
        super("Viatura não encontrada com o id: " +id);
    }
}
