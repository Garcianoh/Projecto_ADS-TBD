-- =============================================================
-- pkg_saldo (BODY)
-- Implementação da lógica de gestão de saldo e pagamentos.
-- =============================================================

CREATE OR REPLACE PACKAGE BODY pkg_saldo IS

    -- Gera referência única: REF-{id_utente}-{timestamp em ms}
    FUNCTION fn_gerar_referencia RETURN VARCHAR2
    IS
    BEGIN
        RETURN LPAD(TRUNC(DBMS_RANDOM.VALUE(100000000, 999999999)), 9, '0');
    END fn_gerar_referencia;


    PROCEDURE pr_consultar_saldo (
        p_id_utente  IN NUMBER,
        p_saldo_out  OUT NUMBER,
        p_moeda_out  OUT VARCHAR2
    )
    IS
    BEGIN
        SELECT saldo INTO p_saldo_out
        FROM conta
        WHERE id_utente = p_id_utente;

        p_moeda_out := 'AOA';

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_saldo_out := NULL;
            p_moeda_out := NULL;
    END pr_consultar_saldo;


    PROCEDURE pr_solicitar_carregamento_referencia (
        p_id_utente        IN NUMBER,
        p_valor            IN NUMBER,
        p_entidade_out     OUT VARCHAR2,
        p_referencia_out   OUT VARCHAR2,
        p_id_transacao_out OUT NUMBER
    )
    IS
        v_id_conta   NUMBER;
        v_referencia VARCHAR2(50);
    BEGIN
        SELECT id_conta INTO v_id_conta
        FROM conta
        WHERE id_utente = p_id_utente;

        v_referencia := fn_gerar_referencia();

        INSERT INTO transacao_pagamento (id_conta, valor, referencia, tipo, estado)
        VALUES (v_id_conta, p_valor, v_referencia, 'REFERENCIA', 'PENDENTE')
        RETURNING id_transacao INTO p_id_transacao_out;

        p_entidade_out   := c_numero_entidade;
        p_referencia_out := v_referencia;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_solicitar_carregamento_referencia;

    PROCEDURE pr_carregar_directo (
        p_id_utente        IN NUMBER,
        p_valor            IN NUMBER,
        p_id_transacao_out OUT NUMBER
    )
    IS
        v_id_conta NUMBER;
    BEGIN
        SELECT id_conta INTO v_id_conta
        FROM conta
        WHERE id_utente = p_id_utente;

        INSERT INTO transacao_pagamento (id_conta, valor, tipo, estado)
        VALUES (v_id_conta, p_valor, 'DIRECTO', 'CONFIRMADO')
        RETURNING id_transacao INTO p_id_transacao_out;

        UPDATE conta
        SET saldo = saldo + p_valor
        WHERE id_conta = v_id_conta;

        COMMIT;
    
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_carregar_directo;


    PROCEDURE pr_confirmar_pagamento_webhook (
        p_referencia IN VARCHAR2,
        p_valor      IN NUMBER,
        p_estado     IN VARCHAR2
    )
    IS
        v_id_conta     NUMBER;
        v_valor_esperado NUMBER;
        v_estado_actual  VARCHAR2(20);
    BEGIN
        SELECT t.id_conta, t.valor, t.estado
        INTO v_id_conta, v_valor_esperado, v_estado_actual
        FROM transacao_pagamento t
        WHERE t.referencia = p_referencia;

        -- Só processa se estiver PENDENTE
        IF v_estado_actual <> 'PENDENTE' THEN
            RAISE_APPLICATION_ERROR(-20051,
                'Transação já foi processada anteriormente.');
        END IF;

        -- Valida que o valor coincide
        IF p_valor <> v_valor_esperado THEN
            RAISE_APPLICATION_ERROR(-20052,
                'Valor do pagamento não coincide com o valor solicitado.');
        END IF;

        IF p_estado = 'CONFIRMADO' THEN
            UPDATE transacao_pagamento
            SET estado = 'CONFIRMADO'
            WHERE referencia = p_referencia;

            UPDATE conta
            SET saldo = saldo + p_valor
            WHERE id_conta = v_id_conta;

        ELSIF p_estado = 'CANCELADO' THEN
            UPDATE transacao_pagamento
            SET estado = 'CANCELADO'
            WHERE referencia = p_referencia;
        END IF;

        COMMIT;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20050, 'Referência de pagamento não encontrada.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_confirmar_pagamento_webhook;


    PROCEDURE pr_debitar_saldo (
        p_id_utente   IN NUMBER,
        p_valor       IN NUMBER,
        p_sucesso_out OUT NUMBER
    )
    IS
        v_saldo_actual NUMBER;
    BEGIN
        SELECT saldo INTO v_saldo_actual
        FROM conta
        WHERE id_utente = p_id_utente
        FOR UPDATE; -- bloqueia a linha para evitar condições de corrida

        IF v_saldo_actual >= p_valor THEN
            UPDATE conta
            SET saldo = saldo - p_valor
            WHERE id_utente = p_id_utente;

            COMMIT;
            p_sucesso_out := 1;
        ELSE
            p_sucesso_out := 0;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_debitar_saldo;

END pkg_saldo;
/