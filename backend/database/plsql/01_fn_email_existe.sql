-- =============================================================
-- fn_email_existe
-- Verifica se já existe um utente registado com o email fornecido.
-- Retorna 1 se existe, 0 se não existe.
-- =============================================================

CREATE OR REPLACE FUNCTION fn_email_existe (
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
/
SELECT fn_email_existe('teste@uan.ao') FROM dual;

SELECT line, position, text 
FROM user_errors 
WHERE name = 'FN_EMAIL_EXISTE';

