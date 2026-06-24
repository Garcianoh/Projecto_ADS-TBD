package com.uan.dasoboleia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitarRecuperacaoRequest {
    
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ter um formato válido")
    private String email;
}
