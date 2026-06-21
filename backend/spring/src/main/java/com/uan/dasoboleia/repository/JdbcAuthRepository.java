package com.uan.dasoboleia.repository;

import com.uan.dasoboleia.dto.UtenteLoginData;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthRepository implements AuthRepository {

    private static final String FN_EMAIL_EXISTE = "fn_email_existe";
    private static final String PR_REGISTAR_UTENTE = "pr_registar_utente";
    private static final String PR_BUSCAR_UTENTE_LOGIN = "pr_buscar_utente_login";

    private final DataSource dataSource;

    @Override
    public boolean emailExiste(String email) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withFunctionName(FN_EMAIL_EXISTE);

        Number resultado = call.executeFunction(
                Number.class,
                new MapSqlParameterSource().addValue("p_email", email)
        );

        return resultado.intValue() == 1;
    }

    @Override
    public Long registarUtente(
            String nome,
            String apelido,
            String numeroUtente,
            String email,
            String passwordHash,
            String categoria,
            String curso
    ) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName(PR_REGISTAR_UTENTE)
                .declareParameters(
                        new org.springframework.jdbc.core.SqlOutParameter("p_id_utente_out", Types.NUMERIC)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_nome", nome)
                .addValue("p_apelido", apelido)
                .addValue("p_numero_utente", numeroUtente)
                .addValue("p_email", email)
                .addValue("p_password_hash", passwordHash)
                .addValue("p_categoria", categoria)
                .addValue("p_curso", curso);

        Map<String, Object> resultado = call.execute(params);

        return ((Number) resultado.get("p_id_utente_out")).longValue();
    }

    @Override
    public Optional<UtenteLoginData> buscarParaLogin(String email) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName(PR_BUSCAR_UTENTE_LOGIN)
                .declareParameters(
                        new org.springframework.jdbc.core.SqlOutParameter("p_id_utente_out", Types.NUMERIC),
                        new org.springframework.jdbc.core.SqlOutParameter("p_password_hash_out", Types.VARCHAR),
                        new org.springframework.jdbc.core.SqlOutParameter("p_categoria_out", Types.VARCHAR)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_email", email);

        Map<String, Object> resultado = call.execute(params);

        Object idUtenteObj = resultado.get("p_id_utente_out");

        if (idUtenteObj == null) {
            return Optional.empty();
        }

        Long idUtente = ((Number) idUtenteObj).longValue();
        String passwordHash = (String) resultado.get("p_password_hash_out");
        String categoria = (String) resultado.get("p_categoria_out");

        return Optional.of(new UtenteLoginData(idUtente, passwordHash, categoria));
    }
}