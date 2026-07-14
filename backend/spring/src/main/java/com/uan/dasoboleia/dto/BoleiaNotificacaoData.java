package com.uan.dasoboleia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BoleiaNotificacaoData {

    private final Long idBoleia;
    private final LocalDate dataInicio;
    private final String origem;
    private final String destino;
    private final String emailCriador;
    private final String nomeCriador;
}