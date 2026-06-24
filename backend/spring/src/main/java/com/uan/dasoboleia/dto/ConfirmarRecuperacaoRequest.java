package com.uan.dasoboleia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmarRecuperacaoRequest {
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ter um formato válido")
    private String email;

    @NotBlank(message = "O codigo é obrigatório")
    @Size(min = 6, max = 6, message = "O codigo deve ter exatamente 6 digitos")
    private String codigo;

    @NotBlank(message = "A nova password é obrigatŕia")
    @Size(min = 8, message = "A password deve ter no minimo 8 caracteres")
    private String novaPassword;
}
