package com.uan.dasoboleia.exception;

public class BoleiaNotFoundException extends RuntimeException {

    public BoleiaNotFoundException(Long id) {
        super("Boleia não encontrada com o id: " + id);
    }
}