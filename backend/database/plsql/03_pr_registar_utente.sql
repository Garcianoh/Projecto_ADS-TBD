-- =============================================================
-- pr_registar_utente
-- Regista um novo utente, resolvendo categoria e curso por nome.
-- Se a categoria for 'Aluno', regista também na tabela aluno.
-- =============================================================

CREATE OR REPLACE PROCEDURE pr_registar_utente (
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
    -- Resolve o id_categoria a partir do nome
    SELECT id_categoria
    INTO v_id_categoria
    FROM categoria
    WHERE titulo = p_categoria;

    -- Insere o utente (o trigger trg_criar_conta_utente cria a conta automaticamente)
    INSERT INTO utente (nome, apelido, numero_utente, email, password, id_categoria)
    VALUES (p_nome, p_apelido, p_numero_utente, p_email, p_password_hash, v_id_categoria)
    RETURNING id_utente INTO p_id_utente_out;

    -- Se a categoria for 'Aluno', resolve o curso e regista na tabela aluno
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
/

DECLARE
    v_id_utente NUMBER;
BEGIN
    pr_registar_utente(
        p_nome          => 'Maria',
        p_apelido       => 'Fernandes',
        p_numero_utente => '2024555',
        p_email         => 'maria.fernandes@uan.ao',
        p_password_hash => 'hash_exemplo_123',
        p_categoria     => 'Aluno',
        p_curso         => 'Ciências da Computação',
        p_id_utente_out => v_id_utente
    );

    DBMS_OUTPUT.PUT_LINE('Utente criado com id: ' || v_id_utente);
END;
/