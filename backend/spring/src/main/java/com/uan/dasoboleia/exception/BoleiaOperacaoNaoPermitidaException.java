package com.uan.dasoboleia.exception;

public class BoleiaOperacaoNaoPermitidaException extends RuntimeException {

    public BoleiaOperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}