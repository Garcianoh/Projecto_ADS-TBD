package com.uan.dasoboleia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Resposta devolvida após registo ou login bem-sucedido.
 */
@Getter
@AllArgsConstructor
public class AuthResponse {
    
    private final String token;
    private final String nick;
    private final String categoria;
}
