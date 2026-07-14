package com.uan.dasoboleia.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

//Dados para simular um pagamento em ambente de desenvolvimento

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimularPagamentoRequest {

    @NotBlank(message = "A referência é obrigatória")
    private String referencia;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser positivo")
    private BigDecimal valor;

    @NotBlank(message = "O estado é obrigatório")
    @Pattern(regexp = "^(CONFIRMADO|CANCELADO)$",
             message = "Estado inválido. Valores aceites: CONFIRMADO, CANCELADO")
    private String estado;
}
