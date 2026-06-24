-- =============================================================
-- pkg_autenticacao
-- Implementação da lógica de autenticação.
-- =============================================================

CREATE OR REPLACE PACKAGE BODY pkg_autenticacao IS

    -- -----------------------------------------------------------
    -- fn_email_existe
    -- -----------------------------------------------------------
    FUNCTION fn_email_existe (
        p_email IN VARCHAR2
    ) RETURN NUMBER
    IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_total
        FROM utente
        WHERE email = p_email;

        IF v_total > 0 THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    END fn_email_existe;


    -- -----------------------------------------------------------
    -- pr_registar_utente
    -- -----------------------------------------------------------
    PROCEDURE pr_registar_utente (
        p_nome           IN VARCHAR2,
        p_apelido        IN VARCHAR2,
        p_numero_utente  IN VARCHAR2,
        p_email          IN VARCHAR2,
        p_password_hash  IN VARCHAR2,
        p_categoria      IN VARCHAR2,
        p_curso          IN VARCHAR2,
        p_id_utente_out  OUT NUMBER
    )
    IS
        v_id_categoria  NUMBER;
        v_id_curso      NUMBER;
    BEGIN
        SELECT id_categoria
        INTO v_id_categoria
        FROM categoria
        WHERE titulo = p_categoria;

        INSERT INTO utente (nome, apelido, numero_utente, email, password, id_categoria)
        VALUES (p_nome, p_apelido, p_numero_utente, p_email, p_password_hash, v_id_categoria)
        RETURNING id_utente INTO p_id_utente_out;

        IF UPPER(p_categoria) = 'ALUNO' THEN

            SELECT id_curso
            INTO v_id_curso
            FROM curso
            WHERE nome = p_curso;

            INSERT INTO aluno (id_curso, id_utente)
            VALUES (v_id_curso, p_id_utente_out);

        END IF;

        COMMIT;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20010, 'Categoria ou curso não encontrado.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_registar_utente;


    -- -----------------------------------------------------------
    -- pr_buscar_utente_login
    -- -----------------------------------------------------------
    PROCEDURE pr_buscar_utente_login (
        p_email              IN VARCHAR2,
        p_id_utente_out      OUT NUMBER,
        p_password_hash_out  OUT VARCHAR2,
        p_categoria_out      OUT VARCHAR2
    )
    IS
    BEGIN
        SELECT u.id_utente, u.password, c.titulo
        INTO p_id_utente_out, p_password_hash_out, p_categoria_out
        FROM utente u
        JOIN categoria c ON c.id_categoria = u.id_categoria
        WHERE u.email = p_email;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_id_utente_out     := NULL;
            p_password_hash_out := NULL;
            p_categoria_out     := NULL;
    END pr_buscar_utente_login;

END pkg_autenticacao;
/