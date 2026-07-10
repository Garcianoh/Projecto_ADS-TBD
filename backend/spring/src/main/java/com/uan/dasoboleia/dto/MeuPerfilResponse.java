package com.uan.dasoboleia.dto;
//dados completo do perfil do utente autenticado, privado

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeuPerfilResponse {
    
    private final Long idUtente;
    private final String nome;
    private final String apelido;
    private final String nick;
    private final String email;
    private final String fotoUrl;
    private final String categoria;
    private final String numeroUtente;
    private final LocalDateTime timestamp;
}
