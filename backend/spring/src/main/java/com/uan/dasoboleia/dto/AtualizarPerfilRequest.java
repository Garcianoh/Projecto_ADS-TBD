package com.uan.dasoboleia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * representa os dados recebidos para atualizar o perfil do utente
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarPerfilRequest {
    
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O apelido é obrigatório")
    private String apelido;

    @NotBlank(message = "O pseudónimo (nick) é obrigatório")
    @Size(min = 3, max = 50, message = "O nick deve ter entre 3 e 50 caracteres")
    private String nick;
}
