-- =============================================================
-- pkg_viatura (SPEC)
-- Pacote responsável pela gestão de viaturas do utente.
-- =============================================================

CREATE OR REPLACE PACKAGE pkg_viatura IS

    /**
     * Verifica se a matrícula já existe.
     * Retorna 1 se existe, 0 se não existe.
     */
    FUNCTION fn_matricula_existe (
        p_matricula IN VARCHAR2
    ) RETURN NUMBER;

    /**
     * Regista uma nova viatura para o utente autenticado.
     * Devolve o id_viatura gerado.
     */
    PROCEDURE pr_registar_viatura (
        p_nome          IN VARCHAR2,
        p_modelo        IN VARCHAR2,
        p_matricula     IN VARCHAR2,
        p_capacidade    IN NUMBER,
        p_id_utente     IN NUMBER,
        p_id_viatura_out OUT NUMBER
    );

    /**
     * Atualiza os dados de uma viatura do utente.
     * Garante que a viatura pertence ao utente antes de atualizar.
     */
    PROCEDURE pr_atualizar_viatura (
        p_id_viatura IN NUMBER,
        p_id_utente  IN NUMBER,
        p_nome       IN VARCHAR2,
        p_modelo     IN VARCHAR2,
        p_matricula  IN VARCHAR2,
        p_capacidade IN NUMBER
    );

    /**
     * Lista todas as viaturas do utente autenticado.
     * Devolve um cursor com os dados.
     */
    PROCEDURE pr_listar_viaturas_utente (
        p_id_utente   IN NUMBER,
        p_cursor_out  OUT SYS_REFCURSOR
    );

    /**
     * Consulta uma viatura específica pelo id.
     * Garante que a viatura pertence ao utente.
     */
    PROCEDURE pr_consultar_viatura (
        p_id_viatura     IN NUMBER,
        p_id_utente      IN NUMBER,
        p_nome_out       OUT VARCHAR2,
        p_modelo_out     OUT VARCHAR2,
        p_matricula_out  OUT VARCHAR2,
        p_capacidade_out OUT NUMBER,
        p_timestamp_out  OUT TIMESTAMP
    );

    /**
     * Elimina uma viatura do utente.
     * Garante que a viatura pertence ao utente antes de eliminar.
     */
    PROCEDURE pr_eliminar_viatura (
        p_id_viatura IN NUMBER,
        p_id_utente  IN NUMBER
    );

END pkg_viatura;
/