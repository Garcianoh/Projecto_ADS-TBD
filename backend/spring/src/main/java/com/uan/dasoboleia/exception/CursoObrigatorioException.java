package com.uan.dasoboleia.exception;

public class CursoObrigatorioException extends RuntimeException {
    
    public CursoObrigatorioException() {
        super("O curso é obrigatório para a categoria 'Aluno'");
    }
}
