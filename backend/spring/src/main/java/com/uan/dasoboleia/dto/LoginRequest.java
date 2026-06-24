package com.uan.dasoboleia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dados recebidos no login de um utente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "O pseudónimo (nick) é obrigatório")
    private String nick;

    @NotBlank(message = "A password é obrigatória")
    private String password;
}
