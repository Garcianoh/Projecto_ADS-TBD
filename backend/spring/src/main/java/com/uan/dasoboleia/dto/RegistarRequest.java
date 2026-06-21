package com.uan.dasoboleia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dados recebidos no registo de um novo utente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistarRequest {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O apelido é obrigatório")
    private String apelido;

    @NotBlank(message = "O número de utente é obrigatório")
    private String numeroUtente;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ter um formato válido")
    private String email;

    @NotBlank(message = "A passeord é obrigatório")
    @Size(min = 8, message = "A password deve ter no mínimo 8 caracteres")
    private String password;

    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    /**
     * Opcional — só deve ser preenchido quando a categoria for "Aluno".
     */
    private String curso;
}
