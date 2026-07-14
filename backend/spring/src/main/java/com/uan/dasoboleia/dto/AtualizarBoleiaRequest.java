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
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarBoleiaRequest {

    @NotNull(message = "O custo é obrigatório")
    @DecimalMin(value = "0.00", message = "O custo não pode ser negativo")
    private BigDecimal custo;

    @NotNull(message = "A data de início é obrigatória")
    private LocalDate dataInicio;

    @NotBlank(message = "O tipo de boleia é obrigatório")
    @Pattern(regexp = "^(UNICA|DIARIA|SEMANAL|MENSAL)$",
             message = "Tipo inválido. Valores: UNICA, DIARIA, SEMANAL, MENSAL")
    private String tipoBoleia;

    @NotNull(message = "O id do trajeto é obrigatório")
    private Long idTrajeto;

    private LocalDate dataFim;
}