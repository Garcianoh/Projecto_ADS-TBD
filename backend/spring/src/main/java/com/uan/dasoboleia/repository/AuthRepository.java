package com.uan.dasoboleia.repository;

import com.uan.dasoboleia.dto.UtenteLoginData;

import java.util.Optional;

/**
 * Contrato de acesso aos dados de autenticação.
 * Toda a implementação delega a escrita/leitura para PL/SQL (procedures/functions).
 */
public interface AuthRepository {

    boolean emailExiste(String email);

    boolean nickExiste(String nick);

    Long registarUtente(
            String nome,
            String apelido,
            String nick,
            String numeroUtente,
            String email,
            String passwordHash,
            String categoria,
            String curso
    );

    /**
     * Busca os dados necessários para validar o login de um utente pelo nick.
     * Devolve vazio se o nick não existir.
     */
    Optional<UtenteLoginData> buscarParaLogin(String nick);

    void registarTentativaFalhada(String nick);

    void resetarTentativas(Long idUtente);
}