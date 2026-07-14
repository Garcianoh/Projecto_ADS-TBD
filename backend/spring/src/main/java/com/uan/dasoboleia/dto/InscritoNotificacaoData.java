package com.uan.dasoboleia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class InscritoNotificacaoData {

    private final String email;
    private final String nome;
    private final LocalDate dataInicio;
    private final String origem;
    private final String destino;
}