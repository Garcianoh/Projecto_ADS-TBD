-- =============================================================
-- pkg_viatura (BODY)
-- Implementação da lógica de gestão de viaturas.
-- =============================================================

CREATE OR REPLACE PACKAGE BODY pkg_viatura IS

    c_capacidade_minima CONSTANT NUMBER := 2;
    c_capacidade_maxima CONSTANT NUMBER := 20;


    FUNCTION fn_matricula_existe (
        p_matricula IN VARCHAR2
    ) RETURN NUMBER
    IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_total
        FROM viatura
        WHERE matricula = p_matricula;

        IF v_total > 0 THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    END fn_matricula_existe;


    PROCEDURE pr_registar_viatura (
        p_nome           IN VARCHAR2,
        p_modelo         IN VARCHAR2,
        p_matricula      IN VARCHAR2,
        p_capacidade     IN NUMBER,
        p_id_utente      IN NUMBER,
        p_id_viatura_out OUT NUMBER
    )
    IS
    BEGIN
        IF p_capacidade < c_capacidade_minima OR p_capacidade > c_capacidade_maxima THEN
            RAISE_APPLICATION_ERROR(-20040,
                'Capacidade inválida. Deve ser entre ' || c_capacidade_minima ||
                ' e ' || c_capacidade_maxima || ' lugares.');
        END IF;

        INSERT INTO viatura (nome, modelo, matricula, capacidade, id_utente)
        VALUES (p_nome, p_modelo, p_matricula, p_capacidade, p_id_utente)
        RETURNING id_viatura INTO p_id_viatura_out;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_registar_viatura;


    PROCEDURE pr_atualizar_viatura (
        p_id_viatura IN NUMBER,
        p_id_utente  IN NUMBER,
        p_nome       IN VARCHAR2,
        p_modelo     IN VARCHAR2,
        p_matricula  IN VARCHAR2,
        p_capacidade IN NUMBER
    )
    IS
        v_total NUMBER;
    BEGIN
        IF p_capacidade < c_capacidade_minima OR p_capacidade > c_capacidade_maxima THEN
            RAISE_APPLICATION_ERROR(-20040,
                'Capacidade inválida. Deve ser entre ' || c_capacidade_minima ||
                ' e ' || c_capacidade_maxima || ' lugares.');
        END IF;

        -- Verifica se a matrícula nova já está em uso por outra viatura
        SELECT COUNT(*)
        INTO v_total
        FROM viatura
        WHERE matricula = p_matricula
        AND id_viatura <> p_id_viatura;

        IF v_total > 0 THEN
            RAISE_APPLICATION_ERROR(-20041, 'A matrícula já está registada.');
        END IF;

        UPDATE viatura
        SET nome       = p_nome,
            modelo     = p_modelo,
            matricula  = p_matricula,
            capacidade = p_capacidade
        WHERE id_viatura = p_id_viatura
        AND id_utente    = p_id_utente;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20042, 'Viatura não encontrada ou não pertence ao utente.');
        END IF;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_atualizar_viatura;


    PROCEDURE pr_listar_viaturas_utente (
        p_id_utente  IN NUMBER,
        p_cursor_out OUT SYS_REFCURSOR
    )
    IS
    BEGIN
        OPEN p_cursor_out FOR
            SELECT id_viatura, nome, modelo, matricula, capacidade, timestamp
            FROM viatura
            WHERE id_utente = p_id_utente
            ORDER BY timestamp DESC;
    END pr_listar_viaturas_utente;


    PROCEDURE pr_consultar_viatura (
        p_id_viatura     IN NUMBER,
        p_id_utente      IN NUMBER,
        p_nome_out       OUT VARCHAR2,
        p_modelo_out     OUT VARCHAR2,
        p_matricula_out  OUT VARCHAR2,
        p_capacidade_out OUT NUMBER,
        p_timestamp_out  OUT TIMESTAMP
    )
    IS
    BEGIN
        SELECT nome, modelo, matricula, capacidade, timestamp
        INTO p_nome_out, p_modelo_out, p_matricula_out,
             p_capacidade_out, p_timestamp_out
        FROM viatura
        WHERE id_viatura = p_id_viatura
        AND id_utente    = p_id_utente;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_nome_out       := NULL;
            p_modelo_out     := NULL;
            p_matricula_out  := NULL;
            p_capacidade_out := NULL;
            p_timestamp_out  := NULL;
    END pr_consultar_viatura;


    PROCEDURE pr_eliminar_viatura (
        p_id_viatura IN NUMBER,
        p_id_utente  IN NUMBER
    )
    IS
    BEGIN
        DELETE FROM viatura
        WHERE id_viatura = p_id_viatura
        AND id_utente    = p_id_utente;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20042, 'Viatura não encontrada ou não pertence ao utente.');
        END IF;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_eliminar_viatura;

END pkg_viatura;
/