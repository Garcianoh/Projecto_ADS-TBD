package com.uan.dasoboleia.repository;

import com.uan.dasoboleia.dto.UtenteLoginData;

import java.util.Optional;

/**
 * Contrato de acesso aos dados de autenticação.
 * Toda a implementação delega a escrita/leitura para PL/SQL (procedures/functions).
 */
public interface AuthRepository {

    /**
     * Verifica se já existe um utente registado com o email fornecido.
     */
    boolean emailExiste(String email);

    /**
     * Regista um novo utente através da procedure PL/SQL e devolve o id gerado.
     */
    Long registarUtente(
            String nome,
            String apelido,
            String numeroUtente,
            String email,
            String passwordHash,
            String categoria,
            String curso
    );

    /**
     * Busca os dados necessários para validar o login de um utente pelo email.
     * Devolve vazio se o email não existir.
     */
    Optional<UtenteLoginData> buscarParaLogin(String email);
}