package com.uan.dasoboleia.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//representa os dados recebidos do sistema de pagamento externo, via webhook

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookPagamentoRequest {
    
    @NotBlank(message = "A referência é obrigatória")
    private String referencia;

    @NotNull(message = "O valor é obrigatorio")
    @DecimalMin(value = "0.01", message = "O valor deve ser positivo")
    private BigDecimal valor;

    @NotBlank(message = "O estado é obrigatório")
    @Pattern(regexp = "^(CONFIRMADO|CANCELADO)$",
            message = "Estado iválido. Valores aceites: CONFIRMADO, CANCELADO")
    private String estado;
}
