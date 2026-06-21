package com.uan.dasoboleia.dto;

import jakarta.validation.constraints.Email;
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
    
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ter um formato válido")
    private String email;

    @NotBlank(message = "A password é obrigatória")
    private String password;
}
