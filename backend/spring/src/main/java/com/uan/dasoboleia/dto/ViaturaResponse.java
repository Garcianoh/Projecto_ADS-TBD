package com.uan.dasoboleia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ViaturaResponse {
    private final Long idViatura;
    private final String nome;
    private final String modelo;
    private final String matricula;
    private final Integer capacidade;
    private final LocalDateTime timestamp;
}
