-- =============================================================
-- pkg_saldo (SPEC)
-- Pacote responsável pela gestão de saldo e pagamentos.
-- =============================================================

CREATE OR REPLACE PACKAGE pkg_saldo IS

    c_numero_entidade CONSTANT VARCHAR2(10) := '00123';

    /**
     * Consulta o saldo actual do utente autenticado.
     */
    PROCEDURE pr_consultar_saldo (
        p_id_utente   IN NUMBER,
        p_saldo_out   OUT NUMBER,
        p_moeda_out   OUT VARCHAR2
    );

    /**
     * Gera uma referência de pagamento para carregamento (Abordagem 2).
     * Cria uma transação PENDENTE e devolve entidade + referência.
     */
    PROCEDURE pr_solicitar_carregamento_referencia (
        p_id_utente      IN NUMBER,
        p_valor          IN NUMBER,
        p_entidade_out   OUT VARCHAR2,
        p_referencia_out OUT VARCHAR2,
        p_id_transacao_out OUT NUMBER
    );

    /**
     * Processa carregamento directo (Abordagem 1).
     * Cria transação e credita saldo imediatamente.
     */
    PROCEDURE pr_carregar_directo (
        p_id_utente        IN NUMBER,
        p_valor            IN NUMBER,
        p_id_transacao_out OUT NUMBER
    );

    /**
     * Confirma pagamento via webhook (Abordagem 2).
     * Valida a referência e credita o saldo.
     */
    PROCEDURE pr_confirmar_pagamento_webhook (
        p_referencia  IN VARCHAR2,
        p_valor       IN NUMBER,
        p_estado      IN VARCHAR2
    );

    /**
     * Debita o saldo do utente.
     * Usado internamente na inscrição em boleia.
     * Retorna 1 se sucesso, 0 se saldo insuficiente.
     */
    PROCEDURE pr_debitar_saldo (
        p_id_utente  IN NUMBER,
        p_valor      IN NUMBER,
        p_sucesso_out OUT NUMBER
    );

END pkg_saldo;
/