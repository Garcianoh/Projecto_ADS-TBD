-- =============================================================
-- pkg_boleia (BODY)
-- Implementação da lógica de gestão de boleias.
-- =============================================================
CREATE OR REPLACE PACKAGE BODY pkg_boleia IS

    c_itens_por_pagina CONSTANT NUMBER := 10;

    -- Função privada: conta inscritos numa boleia (excluindo o criador)
    FUNCTION fn_contar_inscritos (p_id_boleia IN NUMBER) RETURN NUMBER
    IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total
        FROM utente_boleia
        WHERE id_boleia = p_id_boleia;
        RETURN v_total;
    END fn_contar_inscritos;


    -- Função privada: obtém capacidade da viatura do condutor da boleia
    FUNCTION fn_obter_capacidade (p_id_boleia IN NUMBER) RETURN NUMBER
    IS
        v_capacidade NUMBER;
    BEGIN
        SELECT v.capacidade INTO v_capacidade
        FROM viatura v
        JOIN utente_boleia ub ON ub.id_utente = v.id_utente
        WHERE ub.id_boleia   = p_id_boleia
        AND   ub.tipo_utente = 'CONDUTOR'
        AND   v.id_viatura   = (
            SELECT id_viatura FROM utente_boleia
            WHERE id_boleia = p_id_boleia
            AND tipo_utente = 'CONDUTOR'
            AND ROWNUM = 1
        );
        RETURN v_capacidade;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
    END fn_obter_capacidade;

    PROCEDURE pr_criar_boleia (
        p_custo          IN NUMBER,
        p_data_inicio    IN DATE,
        p_tipo_boleia    IN VARCHAR2,
        p_id_trajeto     IN NUMBER,
        p_id_utente      IN NUMBER,
        p_tipo_utente    IN VARCHAR2,
        p_id_viatura     IN NUMBER,
        p_data_fim       IN DATE,
        p_id_boleia_out  OUT NUMBER
    )

    IS
        v_capacidade NUMBER;
    BEGIN
        -- Valida tipo_utente
        IF p_tipo_utente NOT IN ('PASSAGEIRO', 'CONDUTOR') THEN
            RAISE_APPLICATION_ERROR(-20060, 'Tipo de utente inválido.');
        END IF;

        -- Se CONDUTOR, valida que tem viatura registada
        IF p_tipo_utente = 'CONDUTOR' THEN
            IF p_id_viatura IS NULL THEN
                RAISE_APPLICATION_ERROR(-20061,
                    'É necessário indicar a viatura para se inscrever como condutor.');
            END IF;

            SELECT capacidade INTO v_capacidade
            FROM viatura
            WHERE id_viatura = p_id_viatura
            AND id_utente    = p_id_utente;
        END IF;



        -- Cria a boleia
        INSERT INTO boleia (custo, data_inicio, tipo_boleia, estado, id_trajeto)
        VALUES (p_custo, p_data_inicio, p_tipo_boleia, 'ATIVO', p_id_trajeto)
        RETURNING id_boleia INTO p_id_boleia_out;

        -- Cria frequência se necessário
        IF p_tipo_boleia <> 'UNICA' THEN
            INSERT INTO frequencia (data_fim, tipo_frequencia, id_boleia)
            VALUES (p_data_fim, p_tipo_boleia, p_id_boleia_out);
        END IF;

        -- Inscreve o criador
        INSERT INTO utente_boleia (tipo_utente, id_utente, id_boleia)
        VALUES (p_tipo_utente, p_id_utente, p_id_boleia_out);

        COMMIT;

        EXCEPTION
        WHEN NO_DATA_FOUND THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20062,
                'Viatura não encontrada ou não pertence ao utente.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_criar_boleia;


    PROCEDURE pr_atualizar_boleia (
        p_id_boleia   IN NUMBER,
        p_id_utente   IN NUMBER,
        p_custo       IN NUMBER,
        p_data_inicio IN DATE,
        p_tipo_boleia IN VARCHAR2,
        p_id_trajeto  IN NUMBER,
        p_data_fim    IN DATE
    )
    IS
        v_total_inscritos NUMBER;
        v_criador         NUMBER;
    BEGIN
        -- Verifica se o utente foi o primeiro a inscrever-se (criador)
        SELECT id_utente INTO v_criador
        FROM utente_boleia
        WHERE id_boleia = p_id_boleia
        AND ROWNUM = 1
        ORDER BY timestamp;

        IF v_criador <> p_id_utente THEN
            RAISE_APPLICATION_ERROR(-20063,
                'Só o criador da boleia pode actualizá-la.');
        END IF;

        -- Verifica se há outros inscritos além do criador
        SELECT COUNT(*) INTO v_total_inscritos
        FROM utente_boleia
        WHERE id_boleia = p_id_boleia
        AND id_utente  <> p_id_utente;

        IF v_total_inscritos > 0 THEN
            RAISE_APPLICATION_ERROR(-20064,
                'Não é possível actualizar a boleia com outros utentes inscritos.');
        END IF;

        UPDATE boleia
        SET custo       = p_custo,
            data_inicio = p_data_inicio,
            tipo_boleia = p_tipo_boleia,
            id_trajeto  = p_id_trajeto
        WHERE id_boleia = p_id_boleia;

        -- Actualiza frequência
        DELETE FROM frequencia WHERE id_boleia = p_id_boleia;

        IF p_tipo_boleia <> 'UNICA' THEN
            INSERT INTO frequencia (data_fim, tipo_frequencia, id_boleia)
            VALUES (p_data_fim, p_tipo_boleia, p_id_boleia);
        END IF;

        COMMIT;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20065, 'Boleia não encontrada.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_atualizar_boleia;

    PROCEDURE pr_eliminar_boleia (
        p_id_boleia IN NUMBER,
        p_id_utente IN NUMBER
    )
    IS
        v_total_inscritos NUMBER;
        v_criador         NUMBER;
    BEGIN
        SELECT id_utente INTO v_criador
        FROM utente_boleia
        WHERE id_boleia = p_id_boleia
        AND ROWNUM = 1
        ORDER BY timestamp;

        IF v_criador <> p_id_utente THEN
            RAISE_APPLICATION_ERROR(-20063,
                'Só o criador da boleia pode eliminá-la.');
        END IF;

        SELECT COUNT(*) INTO v_total_inscritos
        FROM utente_boleia
        WHERE id_boleia = p_id_boleia
        AND id_utente  <> p_id_utente;

        IF v_total_inscritos > 0 THEN
            RAISE_APPLICATION_ERROR(-20064,
                'Não é possível eliminar a boleia com outros utentes inscritos.');
        END IF;

        DELETE FROM frequencia WHERE id_boleia = p_id_boleia;
        DELETE FROM utente_boleia WHERE id_boleia = p_id_boleia;
        DELETE FROM boleia WHERE id_boleia = p_id_boleia;

        COMMIT;
    
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20065, 'Boleia não encontrada.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_eliminar_boleia;

    PROCEDURE pr_consultar_boleia (
        p_id_boleia           IN NUMBER,
        p_custo_out           OUT NUMBER,
        p_data_inicio_out     OUT DATE,
        p_tipo_boleia_out     OUT VARCHAR2,
        p_estado_out          OUT VARCHAR2,
        p_id_trajeto_out      OUT NUMBER,
        p_origem_out          OUT VARCHAR2,
        p_destino_out         OUT VARCHAR2,
        p_total_inscritos_out OUT NUMBER,
        p_capacidade_out      OUT NUMBER,
        p_timestamp_out       OUT TIMESTAMP
    )
    IS
    BEGIN
        SELECT b.custo, b.data_inicio, b.tipo_boleia, b.estado,
               b.id_trajeto, lo.nome, ld.nome, b.timestamp
        INTO p_custo_out, p_data_inicio_out, p_tipo_boleia_out, p_estado_out,
             p_id_trajeto_out, p_origem_out, p_destino_out, p_timestamp_out
        FROM boleia b
        JOIN trajeto t  ON t.id_trajeto       = b.id_trajeto
        JOIN local lo   ON lo.id_local         = t.id_local_origem
        JOIN local ld   ON ld.id_local         = t.id_local_destino
        WHERE b.id_boleia = p_id_boleia;

        p_total_inscritos_out := fn_contar_inscritos(p_id_boleia);
        p_capacidade_out      := fn_obter_capacidade(p_id_boleia);

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_custo_out           := NULL;
            p_data_inicio_out     := NULL;
            p_tipo_boleia_out     := NULL;
            p_estado_out          := NULL;
            p_id_trajeto_out      := NULL;
            p_origem_out          := NULL;
            p_destino_out         := NULL;
            p_total_inscritos_out := NULL;
            p_capacidade_out      := NULL;
            p_timestamp_out       := NULL;
    END pr_consultar_boleia;

    PROCEDURE pr_inscrever_na_boleia (
        p_id_boleia   IN NUMBER,
        p_id_utente   IN NUMBER,
        p_tipo_utente IN VARCHAR2,
        p_id_viatura  IN NUMBER
    )
    IS
        v_estado          VARCHAR2(10);
        v_custo           NUMBER;
        v_ja_inscrito     NUMBER;
        v_tem_condutor    NUMBER;
        v_total_inscritos NUMBER;
        v_capacidade      NUMBER;
        v_saldo           NUMBER;
        v_sucesso         NUMBER;
    BEGIN
        -- Verifica se a boleia existe e está ATIVA
        SELECT estado, custo INTO v_estado, v_custo
        FROM boleia WHERE id_boleia = p_id_boleia;

        IF v_estado <> 'ATIVO' THEN
            RAISE_APPLICATION_ERROR(-20070, 'Só é possível inscrever em boleias activas.');
        END IF;

        -- Verifica se o utente já está inscrito
        SELECT COUNT(*) INTO v_ja_inscrito
        FROM utente_boleia
        WHERE id_boleia = p_id_boleia
        AND id_utente   = p_id_utente;

        IF v_ja_inscrito > 0 THEN
            RAISE_APPLICATION_ERROR(-20071,
                'Já se encontra inscrito nesta boleia.');
        END IF;

        v_total_inscritos := fn_contar_inscritos(p_id_boleia);
        v_capacidade      := fn_obter_capacidade(p_id_boleia);

        IF p_tipo_utente = 'PASSAGEIRO' THEN

            -- Verifica se há lugar disponível (só quando há condutor)
            IF v_capacidade IS NOT NULL AND v_total_inscritos >= v_capacidade THEN
                RAISE_APPLICATION_ERROR(-20072,
                    'Não há lugares disponíveis nesta boleia.');
            END IF;

            -- Verifica e debita saldo
            SELECT saldo INTO v_saldo
            FROM conta WHERE id_utente = p_id_utente
            FOR UPDATE;

            IF v_saldo < v_custo THEN
                RAISE_APPLICATION_ERROR(-20073,
                    'Saldo insuficiente para se inscrever nesta boleia.');
            END IF;

            UPDATE conta
            SET saldo = saldo - v_custo
            WHERE id_utente = p_id_utente;

        ELSIF p_tipo_utente = 'CONDUTOR' THEN

            -- Verifica se já existe condutor
            SELECT COUNT(*) INTO v_tem_condutor
            FROM utente_boleia
            WHERE id_boleia   = p_id_boleia
            AND tipo_utente   = 'CONDUTOR';

            IF v_tem_condutor > 0 THEN
                RAISE_APPLICATION_ERROR(-20074,
                    'Esta boleia já tem um condutor inscrito.');
            END IF;

            -- Valida viatura
            IF p_id_viatura IS NULL THEN
                RAISE_APPLICATION_ERROR(-20061,
                    'É necessário indicar a viatura para se inscrever como condutor.');
            END IF;

            SELECT capacidade INTO v_capacidade
            FROM viatura
            WHERE id_viatura = p_id_viatura
            AND id_utente    = p_id_utente;

            -- Capacidade da viatura deve ser >= inscritos actuais + 1 (o condutor)
            IF v_capacidade < v_total_inscritos + 1 THEN
                RAISE_APPLICATION_ERROR(-20075,
                    'A capacidade da viatura é insuficiente para os inscritos actuais.');
            END IF;
        
        ELSE
            RAISE_APPLICATION_ERROR(-20060, 'Tipo de utente inválido.');
        END IF;

        INSERT INTO utente_boleia (tipo_utente, id_utente, id_boleia)
        VALUES (p_tipo_utente, p_id_utente, p_id_boleia);

        COMMIT;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20065, 'Boleia ou viatura não encontrada.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_inscrever_na_boleia;


    PROCEDURE pr_cancelar_inscricao (
        p_id_boleia IN NUMBER,
        p_id_utente IN NUMBER
    )
    IS
        v_tipo_utente VARCHAR2(20);
        v_custo       NUMBER;
    BEGIN
        SELECT ub.tipo_utente, b.custo
        INTO v_tipo_utente, v_custo
        FROM utente_boleia ub
        JOIN boleia b ON b.id_boleia = ub.id_boleia
        WHERE ub.id_boleia = p_id_boleia
        AND ub.id_utente   = p_id_utente;

        DELETE FROM utente_boleia
        WHERE id_boleia = p_id_boleia
        AND id_utente   = p_id_utente;

        -- Devolve saldo se era PASSAGEIRO
        IF v_tipo_utente = 'PASSAGEIRO' THEN
            UPDATE conta
            SET saldo = saldo + v_custo
            WHERE id_utente = p_id_utente;
        END IF;

        COMMIT;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20076,
                'Inscrição não encontrada nesta boleia.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_cancelar_inscricao;


    PROCEDURE pr_listar_boleias (
        p_filtro     IN VARCHAR2,
        p_id_origem  IN NUMBER,
        p_id_destino IN NUMBER,
        p_data       IN DATE,
        p_pagina     IN NUMBER,
        p_cursor_out OUT SYS_REFCURSOR,
        p_total_out  OUT NUMBER
    )
    IS
        v_offset NUMBER := p_pagina * c_itens_por_pagina;
    BEGIN
        IF p_filtro = 'DISPONIVEIS' THEN

            SELECT COUNT(DISTINCT b.id_boleia) INTO p_total_out
            FROM boleia b
            WHERE b.estado = 'ATIVO'
            AND EXISTS (
                SELECT 1 FROM utente_boleia ub
                JOIN viatura v ON v.id_utente = ub.id_utente
                    AND v.id_viatura = ub.id_boleia
                WHERE ub.id_boleia   = b.id_boleia
                AND ub.tipo_utente   = 'CONDUTOR'
                AND (SELECT COUNT(*) FROM utente_boleia WHERE id_boleia = b.id_boleia) < v.capacidade
            );

            OPEN p_cursor_out FOR
                SELECT DISTINCT b.id_boleia, b.custo, b.data_inicio, b.tipo_boleia,
                    lo.nome AS origem, ld.nome AS destino,
                    (SELECT COUNT(*) FROM utente_boleia WHERE id_boleia = b.id_boleia) AS total_inscritos,
                    (SELECT MIN(v2.capacidade)
                        FROM viatura v2
                        JOIN utente_boleia ub2 ON ub2.id_utente = v2.id_utente
                        WHERE ub2.id_boleia   = b.id_boleia
                        AND ub2.tipo_utente   = 'CONDUTOR') AS capacidade,
                    b.timestamp
                FROM boleia b
                JOIN trajeto t ON t.id_trajeto = b.id_trajeto
                JOIN local lo  ON lo.id_local  = t.id_local_origem
                JOIN local ld  ON ld.id_local  = t.id_local_destino
                WHERE b.estado = 'ATIVO'
                AND EXISTS (
                    SELECT 1 FROM utente_boleia ub
                    JOIN viatura v ON v.id_utente = ub.id_utente
                    WHERE ub.id_boleia   = b.id_boleia
                    AND ub.tipo_utente   = 'CONDUTOR'
                    AND (SELECT COUNT(*) FROM utente_boleia WHERE id_boleia = b.id_boleia) < v.capacidade
                )
                ORDER BY b.data_inicio
                OFFSET v_offset ROWS FETCH NEXT c_itens_por_pagina ROWS ONLY;


        ELSIF p_filtro = 'INDISPONIVEIS' THEN

            SELECT COUNT(*) INTO p_total_out
            FROM boleia b
            JOIN utente_boleia ub ON ub.id_boleia = b.id_boleia
                AND ub.tipo_utente = 'CONDUTOR'
            JOIN viatura v ON v.id_utente = ub.id_utente
            WHERE b.estado = 'ATIVO'
            AND (SELECT COUNT(*) FROM utente_boleia WHERE id_boleia = b.id_boleia) >= v.capacidade;

            OPEN p_cursor_out FOR
                SELECT b.id_boleia, b.custo, b.data_inicio, b.tipo_boleia,
                    lo.nome AS origem, ld.nome AS destino,
                    (SELECT COUNT(*) FROM utente_boleia WHERE id_boleia = b.id_boleia) AS total_inscritos,
                    v.capacidade,
                    b.timestamp
                FROM boleia b
                JOIN trajeto t ON t.id_trajeto = b.id_trajeto
                JOIN local lo  ON lo.id_local  = t.id_local_origem
                JOIN local ld  ON ld.id_local  = t.id_local_destino
                JOIN utente_boleia ub ON ub.id_boleia = b.id_boleia
                    AND ub.tipo_utente = 'CONDUTOR'
                JOIN viatura v ON v.id_utente = ub.id_utente
                WHERE b.estado = 'ATIVO'
                AND (SELECT COUNT(*) FROM utente_boleia WHERE id_boleia = b.id_boleia) >= v.capacidade
                ORDER BY b.data_inicio
                OFFSET v_offset ROWS FETCH NEXT c_itens_por_pagina ROWS ONLY;

        ELSIF p_filtro = 'POR_TRAJETO' THEN

            SELECT COUNT(*) INTO p_total_out
            FROM boleia b
            JOIN trajeto t ON t.id_trajeto = b.id_trajeto
            WHERE b.estado = 'ATIVO'
            AND t.id_local_origem  = p_id_origem
            AND t.id_local_destino = p_id_destino;

            OPEN p_cursor_out FOR
                SELECT b.id_boleia, b.custo, b.data_inicio, b.tipo_boleia,
                       lo.nome AS origem, ld.nome AS destino,
                       (SELECT COUNT(*) FROM utente_boleia WHERE id_boleia = b.id_boleia) AS total_inscritos,
                       (SELECT v.capacidade 
                            FROM viatura v
                            JOIN utente_boleia ub2 ON ub2.id_utente = v.id_utente
                            WHERE ub2.id_boleia   = b.id_boleia
                            AND ub2.tipo_utente   = 'CONDUTOR'
                            AND ROWNUM = 1) AS capacidade,
                       b.timestamp
                FROM boleia b
                JOIN trajeto t ON t.id_trajeto = b.id_trajeto
                JOIN local lo  ON lo.id_local  = t.id_local_origem
                JOIN local ld  ON ld.id_local  = t.id_local_destino
                WHERE b.estado = 'ATIVO'
                AND t.id_local_origem  = p_id_origem
                AND t.id_local_destino = p_id_destino
                ORDER BY b.data_inicio
                OFFSET v_offset ROWS FETCH NEXT c_itens_por_pagina ROWS ONLY;

        ELSIF p_filtro = 'POR_DATA' THEN

            SELECT COUNT(*) INTO p_total_out
            FROM boleia b
            WHERE b.estado = 'ATIVO'
            AND TRUNC(b.data_inicio) = TRUNC(p_data);

            OPEN p_cursor_out FOR
                SELECT b.id_boleia, b.custo, b.data_inicio, b.tipo_boleia,
                       lo.nome AS origem, ld.nome AS destino,
                       (SELECT COUNT(*) FROM utente_boleia WHERE id_boleia = b.id_boleia) AS total_inscritos,
                       (SELECT v.capacidade 
                            FROM viatura v
                            JOIN utente_boleia ub2 ON ub2.id_utente = v.id_utente
                            WHERE ub2.id_boleia   = b.id_boleia
                            AND ub2.tipo_utente   = 'CONDUTOR'
                            AND ROWNUM = 1) AS capacidade,
                       b.timestamp
                FROM boleia b
                JOIN trajeto t ON t.id_trajeto = b.id_trajeto
                JOIN local lo  ON lo.id_local  = t.id_local_origem
                JOIN local ld  ON ld.id_local  = t.id_local_destino
                WHERE b.estado = 'ATIVO'
                AND TRUNC(b.data_inicio) = TRUNC(p_data)
                ORDER BY b.data_inicio
                OFFSET v_offset ROWS FETCH NEXT c_itens_por_pagina ROWS ONLY;

        END IF;

    END pr_listar_boleias;

    PROCEDURE pr_iniciar_viagem (
        p_id_boleia IN NUMBER,
        p_id_utente IN NUMBER
    )
    IS
        v_e_condutor NUMBER;
        v_estado     VARCHAR2(20);
    BEGIN
        SELECT estado INTO v_estado
        FROM boleia WHERE id_boleia = p_id_boleia;

        IF v_estado <> 'ATIVO' THEN
            RAISE_APPLICATION_ERROR(-20080,
                'Só é possível iniciar uma boleia com estado ATIVO.');
        END IF;

        SELECT COUNT(*) INTO v_e_condutor
        FROM utente_boleia
        WHERE id_boleia   = p_id_boleia
        AND id_utente     = p_id_utente
        AND tipo_utente   = 'CONDUTOR';

        IF v_e_condutor = 0 THEN
            RAISE_APPLICATION_ERROR(-20081,
                'Só o condutor inscrito pode iniciar a viagem.');
        END IF;

        UPDATE boleia
        SET estado = 'EM_CURSO'
        WHERE id_boleia = p_id_boleia;

        COMMIT;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20065, 'Boleia não encontrada.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_iniciar_viagem;


    PROCEDURE pr_concluir_viagem (
        p_id_boleia IN NUMBER,
        p_id_utente IN NUMBER
    )
    IS
        v_e_condutor NUMBER;
        v_estado     VARCHAR2(20);
    BEGIN
        SELECT estado INTO v_estado
        FROM boleia WHERE id_boleia = p_id_boleia;

        IF v_estado <> 'EM_CURSO' THEN
            RAISE_APPLICATION_ERROR(-20082,
                'Só é possível concluir uma boleia em curso.');
        END IF;

        SELECT COUNT(*) INTO v_e_condutor
        FROM utente_boleia
        WHERE id_boleia = p_id_boleia
        AND id_utente   = p_id_utente
        AND tipo_utente = 'CONDUTOR';

        IF v_e_condutor = 0 THEN
            RAISE_APPLICATION_ERROR(-20081,
                'Só o condutor inscrito pode concluir a viagem.');
        END IF;

        UPDATE boleia
        SET estado = 'CONCLUIDA'
        WHERE id_boleia = p_id_boleia;

        COMMIT;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(-20065, 'Boleia não encontrada.');
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END pr_concluir_viagem;


    FUNCTION fn_e_condutor_da_boleia (
        p_id_boleia IN NUMBER,
        p_id_utente IN NUMBER
    ) RETURN NUMBER
    IS
        v_total NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_total
        FROM utente_boleia
        WHERE id_boleia   = p_id_boleia
        AND id_utente     = p_id_utente
        AND tipo_utente   = 'CONDUTOR';

        RETURN v_total;
    END fn_e_condutor_da_boleia;


    PROCEDURE pr_buscar_inscritos_para_notificar (
        p_id_boleia  IN NUMBER,
        p_cursor_out OUT SYS_REFCURSOR
    )
    IS
    BEGIN
        OPEN p_cursor_out FOR
            SELECT u.email, u.nome, b.data_inicio,
                   lo.nome AS origem, ld.nome AS destino
            FROM utente_boleia ub
            JOIN utente u  ON u.id_utente   = ub.id_utente
            JOIN boleia b  ON b.id_boleia   = ub.id_boleia
            JOIN trajeto t ON t.id_trajeto  = b.id_trajeto
            JOIN local lo  ON lo.id_local   = t.id_local_origem
            JOIN local ld  ON ld.id_local   = t.id_local_destino
            WHERE ub.id_boleia = p_id_boleia;
    END pr_buscar_inscritos_para_notificar;


    PROCEDURE pr_boleias_para_notificar_inicio (
        p_minutos    IN NUMBER,
        p_cursor_out OUT SYS_REFCURSOR
    )
    IS
    BEGIN
        OPEN p_cursor_out FOR
            SELECT DISTINCT b.id_boleia, b.data_inicio,
                   lo.nome AS origem, ld.nome AS destino
            FROM boleia b
            JOIN trajeto t ON t.id_trajeto = b.id_trajeto
            JOIN local lo  ON lo.id_local  = t.id_local_origem
            JOIN local ld  ON ld.id_local  = t.id_local_destino
            JOIN utente_boleia ub ON ub.id_boleia = b.id_boleia
            WHERE b.estado = 'ATIVO'
            AND b.data_inicio BETWEEN SYSDATE AND SYSDATE + (p_minutos / 1440);
    END pr_boleias_para_notificar_inicio;


    PROCEDURE pr_boleias_sem_condutor_para_notificar (
        p_minutos    IN NUMBER,
        p_cursor_out OUT SYS_REFCURSOR
    )
    IS
    BEGIN
        OPEN p_cursor_out FOR
            SELECT b.id_boleia, b.data_inicio,
                   lo.nome AS origem, ld.nome AS destino,
                   u.email AS email_criador, u.nome AS nome_criador
            FROM boleia b
            JOIN trajeto t ON t.id_trajeto = b.id_trajeto
            JOIN local lo  ON lo.id_local  = t.id_local_origem
            JOIN local ld  ON ld.id_local  = t.id_local_destino
            JOIN (
                SELECT id_boleia, id_utente
                FROM utente_boleia
                WHERE (id_boleia, timestamp) IN (
                    SELECT id_boleia, MIN(timestamp)
                    FROM utente_boleia
                    GROUP BY id_boleia
                )
            ) primeiro ON primeiro.id_boleia = b.id_boleia
            JOIN utente u ON u.id_utente = primeiro.id_utente
            WHERE b.estado = 'ATIVO'
            AND b.data_inicio BETWEEN SYSDATE AND SYSDATE + (p_minutos / 1440)
            AND NOT EXISTS (
                SELECT 1 FROM utente_boleia
                WHERE id_boleia   = b.id_boleia
                AND tipo_utente   = 'CONDUTOR'
            );
    END pr_boleias_sem_condutor_para_notificar;

END pkg_boleia;
/