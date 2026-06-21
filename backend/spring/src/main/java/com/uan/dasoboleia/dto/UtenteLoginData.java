package com.uan.dasoboleia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * Dados do utente necessários para validar o login,
 * tal como devolvidos pela procedure PL/SQL de busca.
 */

@Getter
@AllArgsConstructor
public class UtenteLoginData {
    
    private final Long idUtente;
    private final String passwordHash;
    private final String categoria;
}
