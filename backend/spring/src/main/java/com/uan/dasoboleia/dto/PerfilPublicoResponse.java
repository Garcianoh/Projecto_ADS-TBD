package com.uan.dasoboleia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

//representa o perfil publico de um utente, visivel para qualquer utente autenticado

@Getter
@AllArgsConstructor
public class PerfilPublicoResponse {
    
    private final Long idUtente;
    private final String nome;
    private final String apelido;
    private final String nick;
    private final String fotoUrl;
    private final String categoria;
    private final LocalDateTime timestamp;
}
