package com.uan.dasoboleia.exception;

public class CategoriaInvalidaException extends RuntimeException {
    
    public CategoriaInvalidaException(String categoria) {
        super("Categoria inválida: " + categoria);
    }
}
