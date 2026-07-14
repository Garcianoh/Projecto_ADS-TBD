package com.uan.dasoboleia.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitarCarregamentoRequest {
    
    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "100.00", message = "O valor mínimo de carregamento é 100 AOA")
    private BigDecimal valor;
}
