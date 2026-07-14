-- =============================================================
-- pkg_boleia (SPEC)
-- Pacote responsável pela gestão de boleias.
-- =============================================================

CREATE OR REPLACE PACKAGE pkg_boleia IS

    /**
     * Cria uma nova boleia e inscreve o criador automaticamente.
     * Se tipo_utente = 'CONDUTOR', valida e associa a viatura.
     */
    PROCEDURE pr_criar_boleia (
        p_custo          IN NUMBER,
        p_data_inicio    IN DATE,
        p_tipo_boleia    IN VARCHAR2,
        p_id_trajeto     IN NUMBER,
        p_id_utente      IN NUMBER,
        p_tipo_utente    IN VARCHAR2,
        p_id_viatura     IN NUMBER,
        p_data_fim       IN DATE,
        p_id_boleia_out  OUT NUMBER
    );

    /**
     * Atualiza os dados de uma boleia.
     * Só permitido ao criador e se não houver outros inscritos.
     */
    PROCEDURE pr_atualizar_boleia (
        p_id_boleia   IN NUMBER,
        p_id_utente   IN NUMBER,
        p_custo       IN NUMBER,
        p_data_inicio IN DATE,
        p_tipo_boleia IN VARCHAR2,
        p_id_trajeto  IN NUMBER,
        p_data_fim    IN DATE
    );

    /**
     * Elimina uma boleia.
     * Só permitido ao criador e se não houver outros inscritos.
     */
    PROCEDURE pr_eliminar_boleia (
        p_id_boleia IN NUMBER,
        p_id_utente IN NUMBER
    );

    /**
     * Consulta os detalhes de uma boleia pelo id.
     */
    PROCEDURE pr_consultar_boleia (
        p_id_boleia          IN NUMBER,
        p_custo_out          OUT NUMBER,
        p_data_inicio_out    OUT DATE,
        p_tipo_boleia_out    OUT VARCHAR2,
        p_estado_out         OUT VARCHAR2,
        p_id_trajeto_out     OUT NUMBER,
        p_origem_out         OUT VARCHAR2,
        p_destino_out        OUT VARCHAR2,
        p_total_inscritos_out OUT NUMBER,
        p_capacidade_out     OUT NUMBER,
        p_timestamp_out      OUT TIMESTAMP
    );

    /**
     * Inscreve um utente numa boleia como PASSAGEIRO ou CONDUTOR.
     */
    PROCEDURE pr_inscrever_na_boleia (
        p_id_boleia   IN NUMBER,
        p_id_utente   IN NUMBER,
        p_tipo_utente IN VARCHAR2,
        p_id_viatura  IN NUMBER
    );

    /**
     * Cancela a inscrição de um utente numa boleia.
     * Se era PASSAGEIRO, devolve o custo ao saldo.
     */
    PROCEDURE pr_cancelar_inscricao (
        p_id_boleia IN NUMBER,
        p_id_utente IN NUMBER
    );

    /**
     * Lista boleias paginadas (10 em 10).
     * filtro: 'DISPONIVEIS', 'INDISPONIVEIS', 'POR_TRAJETO', 'POR_DATA'
     */
    PROCEDURE pr_listar_boleias (
        p_filtro         IN VARCHAR2,
        p_id_origem      IN NUMBER,
        p_id_destino     IN NUMBER,
        p_data           IN DATE,
        p_pagina         IN NUMBER,
        p_cursor_out     OUT SYS_REFCURSOR,
        p_total_out      OUT NUMBER
    );

    /**
     * Inicia a viagem — muda estado para 'EM_CURSO'.
     * Só o condutor inscrito pode iniciar.
     */
    PROCEDURE pr_iniciar_viagem (
        p_id_boleia IN NUMBER,
        p_id_utente IN NUMBER
    );

    /**
     * Conclui a viagem — muda estado para 'CONCLUIDA'.
     * Só o condutor inscrito pode concluir.
     */
    PROCEDURE pr_concluir_viagem (
        p_id_boleia IN NUMBER,
        p_id_utente IN NUMBER
    );

    /**
     * Verifica se o utente é o condutor activo da boleia.
     * Retorna 1 se sim, 0 se não.
     */
    FUNCTION fn_e_condutor_da_boleia (
        p_id_boleia IN NUMBER,
        p_id_utente IN NUMBER
    ) RETURN NUMBER;

    /**
     * Busca emails de todos os inscritos numa boleia.
     * Usado pelo scheduler para enviar notificações.
     */
    PROCEDURE pr_buscar_inscritos_para_notificar (
        p_id_boleia  IN NUMBER,
        p_cursor_out OUT SYS_REFCURSOR
    );

    /**
     * Busca boleias que começam em X minutos com inscritos (notificação A).
     */
    PROCEDURE pr_boleias_para_notificar_inicio (
        p_minutos    IN NUMBER,
        p_cursor_out OUT SYS_REFCURSOR
    );

    /**
     * Busca boleias que começam em X minutos sem condutor (notificação B).
     */
    PROCEDURE pr_boleias_sem_condutor_para_notificar (
        p_minutos    IN NUMBER,
        p_cursor_out OUT SYS_REFCURSOR
    );

END pkg_boleia;
/