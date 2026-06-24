CREATE OR REPLACE PACKAGE BODY pkg_autenticacao IS

    c_max_tentativas CONSTANT NUMBER := 5;
    c_minutos_bloqueio CONSTANT NUMBER := 15;
    c_minutos_validade_codigo CONSTANT NUMBER := 10;


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


    FUNCTION fn_nick_existe (
        p_nick IN VARCHAR2
    ) RETURN NUMBER
    IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_total
        FROM utente
        WHERE nick = p_nick;

        IF v_total > 0 THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    END fn_nick_existe;


    PROCEDURE pr_registar_utente (
        p_nome           IN VARCHAR2,
        p_apelido        IN VARCHAR2,
        p_nick           IN VARCHAR2,
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

        INSERT INTO utente (nome, apelido, nick, numero_utente, email, password, id_categoria)
        VALUES (p_nome, p_apelido, p_nick, p_numero_utente, p_email, p_password_hash, v_id_categoria)
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


    PROCEDURE pr_buscar_utente_login (
        p_nick                  IN VARCHAR2,
        p_id_utente_out         OUT NUMBER,
        p_password_hash_out     OUT VARCHAR2,
        p_categoria_out         OUT VARCHAR2,
        p_email_out             OUT VARCHAR2,
        p_bloqueado_out         OUT NUMBER,
        p_minutos_restantes_out OUT NUMBER
    )
    IS
        v_bloqueado_ate TIMESTAMP;
    BEGIN
        SELECT u.id_utente, u.password, c.titulo, u.email, u.bloqueado_ate
        INTO p_id_utente_out, p_password_hash_out, p_categoria_out, p_email_out, v_bloqueado_ate
        FROM utente u
        JOIN categoria c ON c.id_categoria = u.id_categoria
        WHERE u.nick = p_nick;

        IF v_bloqueado_ate IS NOT NULL AND v_bloqueado_ate > SYSTIMESTAMP THEN
            p_bloqueado_out := 1;
            p_minutos_restantes_out := CEIL(
                (CAST(v_bloqueado_ate AS DATE) - SYSDATE) * 24 * 60
            );
            p_password_hash_out := NULL;
            p_categoria_out := NULL;
        ELSE
            p_bloqueado_out := 0;
            p_minutos_restantes_out := 0;
        END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_id_utente_out         := NULL;
            p_password_hash_out     := NULL;
            p_categoria_out         := NULL;
            p_email_out             := NULL;
            p_bloqueado_out         := 0;
            p_minutos_restantes_out := 0;
    END pr_buscar_utente_login;


    PROCEDURE pr_registar_tentativa_falhada (
        p_nick IN VARCHAR2
    )
    IS
        v_tentativas NUMBER;
    BEGIN
        UPDATE utente
        SET tentativas_falhadas = LEAST(tentativas_falhadas + 1, c_max_tentativas)
        WHERE nick = p_nick
        RETURNING tentativas_falhadas INTO v_tentativas;

        IF v_tentativas >= c_max_tentativas THEN
            UPDATE utente
            SET bloqueado_ate = SYSTIMESTAMP + NUMTODSINTERVAL(c_minutos_bloqueio, 'MINUTE')
            WHERE nick = p_nick;
        END IF;

        COMMIT;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL;
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_registar_tentativa_falhada;


    PROCEDURE pr_resetar_tentativas (
        p_id_utente IN NUMBER
    )
    IS
    BEGIN
        UPDATE utente
        SET tentativas_falhadas = 0,
            bloqueado_ate = NULL
        WHERE id_utente = p_id_utente;

        COMMIT;
    END pr_resetar_tentativas;


    PROCEDURE pr_gerar_codigo_recuperacao (
        p_email      IN VARCHAR2,
        p_codigo_out OUT VARCHAR2,
        p_nome_out   OUT VARCHAR2
    )
    IS
        v_codigo VARCHAR2(6);
    BEGIN
        v_codigo := LPAD(TRUNC(DBMS_RANDOM.VALUE(0, 999999)), 6, '0');

        UPDATE utente
        SET codigo_recuperacao = v_codigo,
            codigo_expira_em = SYSTIMESTAMP + NUMTODSINTERVAL(c_minutos_validade_codigo, 'MINUTE')
        WHERE email = p_email
        RETURNING nome INTO p_nome_out;

        IF SQL%ROWCOUNT > 0 THEN
            p_codigo_out := v_codigo;
            COMMIT;
        ELSE
            p_codigo_out := NULL;
            p_nome_out := NULL;
        END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_codigo_out := NULL;
            p_nome_out := NULL;
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_gerar_codigo_recuperacao;


    PROCEDURE pr_validar_codigo_recuperacao (
        p_email      IN VARCHAR2,
        p_codigo     IN VARCHAR2,
        p_valido_out OUT NUMBER
    )
    IS
        v_codigo_guardado VARCHAR2(6);
        v_expira_em       TIMESTAMP;
    BEGIN
        SELECT codigo_recuperacao, codigo_expira_em
        INTO v_codigo_guardado, v_expira_em
        FROM utente
        WHERE email = p_email;

        IF v_codigo_guardado IS NOT NULL
           AND v_codigo_guardado = p_codigo
           AND v_expira_em > SYSTIMESTAMP THEN
            p_valido_out := 1;
        ELSE
            p_valido_out := 0;
        END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_valido_out := 0;
    END pr_validar_codigo_recuperacao;


    PROCEDURE pr_redefinir_password (
        p_email              IN VARCHAR2,
        p_password_hash_novo IN VARCHAR2
    )
    IS
    BEGIN
        UPDATE utente
        SET password = p_password_hash_novo,
            codigo_recuperacao = NULL,
            codigo_expira_em = NULL,
            tentativas_falhadas = 0,
            bloqueado_ate = NULL
        WHERE email = p_email;

        COMMIT;
    END pr_redefinir_password;

END pkg_autenticacao;
/