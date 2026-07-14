package com.uan.dasoboleia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CarregamentoReferenciaResponse {

    private final Long idTransacao;
    private final String entidade;
    private final String referencia;
    private final BigDecimal valor;
    private final String moeda;
    private final String instrucoes;
}