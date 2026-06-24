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

    PROCEDURE pr_buscar_utente_login (
        p_nick                  IN VARCHAR2,
        p_id_utente_out         OUT NUMBER,
        p_password_hash_out     OUT VARCHAR2,
        p_categoria_out         OUT VARCHAR2,
        p_email_out             OUT VARCHAR2,
        p_bloqueado_out         OUT NUMBER,
        p_minutos_restantes_out OUT NUMBER
    );

    PROCEDURE pr_registar_tentativa_falhada (
        p_nick IN VARCHAR2
    );

    PROCEDURE pr_resetar_tentativas (
        p_id_utente IN NUMBER
    );

    /**
     * Gera um código de 6 dígitos para o email fornecido e grava
     * com expiração de 10 minutos. Devolve o código gerado (para envio)
     * e o nome do utente (para personalizar o email).
     * Se o email não existir, os OUT ficam NULL (não revela existência).
     */
    PROCEDURE pr_gerar_codigo_recuperacao (
        p_email          IN VARCHAR2,
        p_codigo_out      OUT VARCHAR2,
        p_nome_out        OUT VARCHAR2
    );

    /**
     * Valida se o código corresponde ao email e ainda não expirou.
     * Devolve p_valido_out = 1 se válido, 0 caso contrário.
     */
    PROCEDURE pr_validar_codigo_recuperacao (
        p_email      IN VARCHAR2,
        p_codigo     IN VARCHAR2,
        p_valido_out OUT NUMBER
    );

    /**
     * Redefine a password do utente e invalida o código usado.
     * Assume que o código já foi validado antes desta chamada.
     */
    PROCEDURE pr_redefinir_password (
        p_email             IN VARCHAR2,
        p_password_hash_novo IN VARCHAR2
    );

END pkg_autenticacao;
/