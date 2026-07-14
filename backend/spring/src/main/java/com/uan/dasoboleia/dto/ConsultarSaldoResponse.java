package com.uan.dasoboleia.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConsultarSaldoResponse {
    
    private final BigDecimal saldo;
    private final String moeda;
}
