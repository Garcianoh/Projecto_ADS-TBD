package com.uan.dasoboleia.exception;

public class MatriculaJaExisteException extends RuntimeException {
    
    public MatriculaJaExisteException(String matricula) {
        super("Já existe uma viatura registada com a matricula:" + matricula);
    }
}
