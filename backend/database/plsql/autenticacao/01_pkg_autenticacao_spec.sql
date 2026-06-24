CREATE OR REPLACE PACKAGE pkg_autenticacao IS

    FUNCTION fn_email_existe (
        p_email IN VARCHAR2
    ) RETURN NUMBER;

    FUNCTION fn_nick_existe (
        p_nick IN VARCHAR2
    ) RETURN NUMBER;

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
    );

    /**
     * Busca os dados de login. Se a conta estiver bloqueada, devolve
     * p_bloqueado_out = 1 e p_id_utente_out = NULL (não revela dados).
     */
    PROCEDURE pr_buscar_utente_login (
        p_nick               IN VARCHAR2,
        p_id_utente_out      OUT NUMBER,
        p_password_hash_out  OUT VARCHAR2,
        p_categoria_out      OUT VARCHAR2,
        p_email_out          OUT VARCHAR2,
        p_bloqueado_out      OUT NUMBER,
        p_minutos_restantes_out OUT NUMBER
    );

    /**
     * Regista uma tentativa de login falhada. Incrementa o contador
     * e bloqueia a conta se atingir o limite de 5 tentativas.
     */
    PROCEDURE pr_registar_tentativa_falhada (
        p_nick IN VARCHAR2
    );

    /**
     * Reseta o contador de tentativas falhadas após login bem-sucedido.
     */
    PROCEDURE pr_resetar_tentativas (
        p_id_utente IN NUMBER
    );

END pkg_autenticacao;
/