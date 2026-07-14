package com.uan.dasoboleia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BoleiaResponse {

    private final Long idBoleia;
    private final BigDecimal custo;
    private final LocalDate dataInicio;
    private final String tipoBoleia;
    private final String estado;
    private final Long idTrajeto;
    private final String origem;
    private final String destino;
    private final Integer totalInscritos;
    private final Integer capacidade;
    private final LocalDateTime timestamp;
}