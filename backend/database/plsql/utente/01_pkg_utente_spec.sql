-- pacote responsável apenas pela gestão do perfil do utente
CREATE OR REPLACE PACKAGE pkg_utente IS

    /**
    * Buscar os dados do perfil de utente pelo seu ID.
    */
    PROCEDURE pr_buscar_utente_por_id (
        p_id_utente         IN NUMBER,
        p_nome_out          OUT VARCHAR2,
        p_apelido_out       OUT VARCHAR2,
        p_nick_out          OUT VARCHAR2,
        p_email_out         OUT VARCHAR2,
        p_foto_url_out      OUT VARCHAR2,
        p_categoria_out     OUT VARCHAR2,
        p_timestamp_out     OUT TIMESTAMP
    );

    /**
    * Busca os dados do perfil do utente autenticado pelo seu ID.
    * Usado para GET/utente/me, sempre privado.
    */
    PROCEDURE pr_buscar_meu_perfil (
        p_id_utente         IN NUMBER,
        p_nome_out          OUT VARCHAR2,
        p_apelido_out       OUT VARCHAR2,
        p_nick_out          OUT VARCHAR2,
        p_email_out         OUT VARCHAR2,
        p_foto_url_out      OUT VARCHAR2,
        p_categoria_out     OUT VARCHAR2,
        p_numero_utente_out OUT VARCHAR2,
        p_timestamp_out     OUT TIMESTAMP
    );

    /**
    *Atualiza os dados do perfil do utente
    */
    PROCEDURE pr_atualizar_utente (
        p_id_utente         IN NUMBER,
        p_nome              IN VARCHAR2,
        p_apelido           IN VARCHAR2,
        p_nick              IN VARCHAR2
    );

    /**
    * atualiza o caminho ou seja a URL da foto de perfil do utente
    */
    PROCEDURE pr_atualizar_foto (
        p_id_utente     IN NUMBER,
        p_foto_url      IN VARCHAR2
    );

    /**
    * Elimina a conta do utente e todos os dados associados.
    */
    PROCEDURE pr_eliminar_utente (
        p_id_utente     IN NUMBER
    );

END pkg_utente;