package com.uan.dasoboleia.repository;

import com.uan.dasoboleia.dto.UtenteLoginData;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthRepository implements AuthRepository {

    private static final String PACKAGE_AUTENTICACAO = "PKG_AUTENTICACAO";

    private static final String FN_EMAIL_EXISTE = "FN_EMAIL_EXISTE";
    private static final String FN_NICK_EXISTE = "FN_NICK_EXISTE";
    private static final String PR_REGISTAR_UTENTE = "PR_REGISTAR_UTENTE";
    private static final String PR_BUSCAR_UTENTE_LOGIN = "PR_BUSCAR_UTENTE_LOGIN";
    private static final String PR_REGISTAR_TENTATIVA_FALHADA = "PR_REGISTAR_TENTATIVA_FALHADA";
    private static final String PR_RESETAR_TENTATIVAS = "PR_RESETAR_TENTATIVAS";

    private final DataSource dataSource;

    @Override
    public boolean emailExiste(String email) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_AUTENTICACAO)
                .withFunctionName(FN_EMAIL_EXISTE);

        Number resultado = call.executeFunction(
                Number.class,
                new MapSqlParameterSource().addValue("p_email", email)
        );

        return resultado.intValue() == 1;
    }

    @Override
    public boolean nickExiste(String nick) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_AUTENTICACAO)
                .withFunctionName(FN_NICK_EXISTE);

        Number resultado = call.executeFunction(
                Number.class,
                new MapSqlParameterSource().addValue("p_nick", nick)
        );

        return resultado.intValue() == 1;
    }

    @Override
    public Long registarUtente(
            String nome,
            String apelido,
            String nick,
            String numeroUtente,
            String email,
            String passwordHash,
            String categoria,
            String curso
    ) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_AUTENTICACAO)
                .withProcedureName(PR_REGISTAR_UTENTE)
                .declareParameters(
                        new SqlOutParameter("p_id_utente_out", Types.NUMERIC)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_nome", nome)
                .addValue("p_apelido", apelido)
                .addValue("p_nick", nick)
                .addValue("p_numero_utente", numeroUtente)
                .addValue("p_email", email)
                .addValue("p_password_hash", passwordHash)
                .addValue("p_categoria", categoria)
                .addValue("p_curso", curso);

        Map<String, Object> resultado = call.execute(params);

        return ((Number) resultado.get("p_id_utente_out")).longValue();
    }

    @Override
    public Optional<UtenteLoginData> buscarParaLogin(String nick) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_AUTENTICACAO)
                .withProcedureName(PR_BUSCAR_UTENTE_LOGIN)
                .declareParameters(
                        new SqlOutParameter("p_id_utente_out", Types.NUMERIC),
                        new SqlOutParameter("p_password_hash_out", Types.VARCHAR),
                        new SqlOutParameter("p_categoria_out", Types.VARCHAR),
                        new SqlOutParameter("p_email_out", Types.VARCHAR),
                        new SqlOutParameter("p_bloqueado_out", Types.NUMERIC),
                        new SqlOutParameter("p_minutos_restantes_out", Types.NUMERIC)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_nick", nick);

        Map<String, Object> resultado = call.execute(params);

        Object idUtenteObj = resultado.get("p_id_utente_out");

        if (idUtenteObj == null) {
            return Optional.empty();
        }

        Long idUtente = ((Number) idUtenteObj).longValue();
        String passwordHash = (String) resultado.get("p_password_hash_out");
        String categoria = (String) resultado.get("p_categoria_out");
        String email = (String) resultado.get("p_email_out");
        boolean bloqueado = ((Number) resultado.get("p_bloqueado_out")).intValue() == 1;
        int minutosRestantes = ((Number) resultado.get("p_minutos_restantes_out")).intValue();

        return Optional.of(new UtenteLoginData(idUtente, passwordHash, categoria, email, bloqueado, minutosRestantes));
    }

    @Override
    public void registarTentativaFalhada(String nick) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_AUTENTICACAO)
                .withProcedureName(PR_REGISTAR_TENTATIVA_FALHADA);

        call.execute(new MapSqlParameterSource().addValue("p_nick", nick));
    }

    @Override
    public void resetarTentativas(Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_AUTENTICACAO)
                .withProcedureName(PR_RESETAR_TENTATIVAS);

        call.execute(new MapSqlParameterSource().addValue("p_id_utente", idUtente));
    }
}