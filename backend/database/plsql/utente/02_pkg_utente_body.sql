-- implementação da lógica do pacote responsavel pela gestão de utente

CREATE OR REPLACE PACKAGE BODY pkg_utente IS

    PROCEDURE pr_buscar_utente_por_id (
        p_id_utente             IN NUMBER,
        p_nome_out              OUT VARCHAR2,
        p_apelido_out           OUT VARCHAR2,
        p_nick_out              OUT VARCHAR2,
        p_email_out             OUT VARCHAR2,
        p_foto_url_out          OUT VARCHAR2,
        p_categoria_out         OUT VARCHAR2,
        p_timestamp_out         OUT TIMESTAMP
    )
    IS
    BEGIN
        SELECT u.nome, u.apelido, u.nick, u.email, u.foto_url, c.titulo, u.timestamp
        INTO p_nome_out, p_apelido_out, p_nick_out, p_email_out, p_foto_url_out, p_categoria_out, p_timestamp_out
        FROM utente u
        JOIN categoria c ON c.id_categoria = u.id_categoria
        WHERE u.id_utente = p_id_utente;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_nome_out      := NULL;
            p_apelido_out   := NULL;
            p_nick_out      := NULL;
            p_email_out     := NULL;
            p_foto_url_out  := NULL;
            p_categoria_out := NULL;
            p_timestamp_out := NULL;
    END pr_buscar_utente_por_id;



    PROCEDURE pr_buscar_meu_perfil (
        p_id_utente          IN NUMBER,
        p_nome_out           OUT VARCHAR2,
        p_apelido_out        OUT VARCHAR2,
        p_nick_out           OUT VARCHAR2,
        p_email_out          OUT VARCHAR2,
        p_foto_url_out       OUT VARCHAR2,
        p_categoria_out      OUT VARCHAR2,
        p_numero_utente_out  OUT VARCHAR2,
        p_timestamp_out      OUT TIMESTAMP
    )
    IS
    BEGIN
        SELECT u.nome, u.apelido, u.nick, u.email, u.foto_url,
               c.titulo, u.numero_utente, u.timestamp
        INTO p_nome_out, p_apelido_out, p_nick_out, p_email_out,
             p_foto_url_out, p_categoria_out, p_numero_utente_out,
             p_timestamp_out
        FROM utente u
        JOIN categoria c ON c.id_categoria = u.id_categoria
        WHERE u.id_utente = p_id_utente;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_nome_out          := NULL;
            p_apelido_out       := NULL;
            p_nick_out          := NULL;
            p_email_out         := NULL;
            p_foto_url_out      := NULL;
            p_categoria_out     := NULL;
            p_numero_utente_out := NULL;
            p_timestamp_out     := NULL;
    END pr_buscar_meu_perfil;


    PROCEDURE pr_atualizar_utente (
        p_id_utente  IN NUMBER,
        p_nome       IN VARCHAR2,
        p_apelido    IN VARCHAR2,
        p_nick       IN VARCHAR2
    )
    IS
        v_total NUMBER;
    BEGIN
        --verificar se o nickname já está em uso
        SELECT COUNT(*)
        INTO v_total
        FROM utente
        WHERE nick = p_nick
        AND id_utente <> p_id_utente;

        IF v_total > 0 THEN
            RAISE_APPLICATION_ERROR(-20030, 'O pseudónimo (nick) já está em uso');
        END IF;

        
        UPDATE utente
        SET nome    = p_nome,
            apelido = p_apelido,
            nick    = p_nick
        WHERE id_utente = p_id_utente;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20020, 'Utente não encontrado.');
        END IF;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_atualizar_utente;


    PROCEDURE pr_atualizar_foto (
        p_id_utente  IN NUMBER,
        p_foto_url   IN VARCHAR2
    )
    IS
    BEGIN
        UPDATE utente
        SET foto_url = p_foto_url
        WHERE id_utente = p_id_utente;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20021, 'Utente não encontrado.');
        END IF;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_atualizar_foto;


    PROCEDURE pr_eliminar_utente (
        p_id_utente IN NUMBER
    )
    IS
    BEGIN
        -- Remove registos dependentes primeiro (ordem inversa das FK)
        DELETE FROM utente_boleia WHERE id_utente = p_id_utente;
        DELETE FROM aluno WHERE id_utente = p_id_utente;
        DELETE FROM viatura WHERE id_utente = p_id_utente;
        DELETE FROM conta WHERE id_utente = p_id_utente;
        DELETE FROM utente WHERE id_utente = p_id_utente;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20022, 'Utente não encontrado.');
        END IF;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_eliminar_utente;

END pkg_utente;