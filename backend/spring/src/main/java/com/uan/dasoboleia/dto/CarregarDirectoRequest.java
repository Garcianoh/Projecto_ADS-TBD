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
public class CarregarDirectoRequest {
    
    @NotNull(message = "O valor é obrigatorio")
    @DecimalMin(value = "100.00", message = "O valor mínimo de carregamento e 100 AOA")
    private BigDecimal valor;
}
