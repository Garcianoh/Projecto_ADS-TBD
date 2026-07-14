package com.uan.dasoboleia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InscricaoBoleiaRequest {

    @NotBlank(message = "O tipo de utente é obrigatório")
    @Pattern(regexp = "^(PASSAGEIRO|CONDUTOR)$",
             message = "Tipo de utente inválido. Valores: PASSAGEIRO, CONDUTOR")
    private String tipoUtente;

    private Long idViatura;
}