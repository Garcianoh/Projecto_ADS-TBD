package com.uan.dasoboleia.repository;

import com.uan.dasoboleia.dto.MeuPerfilResponse;
import com.uan.dasoboleia.dto.PerfilPublicoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcUtenteRepository implements UtenteRepository {

    private static final String PACKAGE_UTENTE = "PKG_UTENTE";

    private static final String PR_BUSCAR_UTENTE_POR_ID = "PR_BUSCAR_UTENTE_POR_ID";
    private static final String PR_BUSCAR_MEU_PERFIL = "PR_BUSCAR_MEU_PERFIL";
    private static final String PR_ATUALIZAR_UTENTE = "PR_ATUALIZAR_UTENTE";
    private static final String PR_ATUALIZAR_FOTO = "PR_ATUALIZAR_FOTO";
    private static final String PR_ELIMINAR_UTENTE = "PR_ELIMINAR_UTENTE";

    private final DataSource dataSource;

    @Override
    public Optional<PerfilPublicoResponse> buscarPorId(Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_UTENTE)
                .withProcedureName(PR_BUSCAR_UTENTE_POR_ID)
                .declareParameters(
                        new SqlOutParameter("p_nome_out", Types.VARCHAR),
                        new SqlOutParameter("p_apelido_out", Types.VARCHAR),
                        new SqlOutParameter("p_nick_out", Types.VARCHAR),
                        new SqlOutParameter("p_email_out", Types.VARCHAR),
                        new SqlOutParameter("p_foto_url_out", Types.VARCHAR),
                        new SqlOutParameter("p_categoria_out", Types.VARCHAR),
                        new SqlOutParameter("p_timestamp_out", Types.TIMESTAMP)
                );

        Map<String, Object> resultado = call.execute(
                new MapSqlParameterSource().addValue("p_id_utente", idUtente)
        );

        String nome = (String) resultado.get("p_nome_out");

        if (nome == null) {
            return Optional.empty();
        }

        Timestamp ts = (Timestamp) resultado.get("p_timestamp_out");

        return Optional.of(new PerfilPublicoResponse(
                idUtente,
                nome,
                (String) resultado.get("p_apelido_out"),
                (String) resultado.get("p_nick_out"),
                (String) resultado.get("p_foto_url_out"),
                (String) resultado.get("p_categoria_out"),
                ts != null ? ts.toLocalDateTime() : null
        ));
    }

    @Override
    public Optional<MeuPerfilResponse> buscarMeuPerfil(Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_UTENTE)
                .withProcedureName(PR_BUSCAR_MEU_PERFIL)
                .declareParameters(
                        new SqlOutParameter("p_nome_out", Types.VARCHAR),
                        new SqlOutParameter("p_apelido_out", Types.VARCHAR),
                        new SqlOutParameter("p_nick_out", Types.VARCHAR),
                        new SqlOutParameter("p_email_out", Types.VARCHAR),
                        new SqlOutParameter("p_foto_url_out", Types.VARCHAR),
                        new SqlOutParameter("p_categoria_out", Types.VARCHAR),
                        new SqlOutParameter("p_numero_utente_out", Types.VARCHAR),
                        new SqlOutParameter("p_timestamp_out", Types.TIMESTAMP)
                );

        Map<String, Object> resultado = call.execute(
                new MapSqlParameterSource().addValue("p_id_utente", idUtente)
        );

        String nome = (String) resultado.get("p_nome_out");

        if (nome == null) {
            return Optional.empty();
        }

        Timestamp ts = (Timestamp) resultado.get("p_timestamp_out");

        return Optional.of(new MeuPerfilResponse(
                idUtente,
                nome,
                (String) resultado.get("p_apelido_out"),
                (String) resultado.get("p_nick_out"),
                (String) resultado.get("p_email_out"),
                (String) resultado.get("p_foto_url_out"),
                (String) resultado.get("p_categoria_out"),
                (String) resultado.get("p_numero_utente_out"),
                ts != null ? ts.toLocalDateTime() : null
        ));
    }

    @Override
    public void atualizarPerfil(Long idUtente, String nome, String apelido, String nick) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_UTENTE)
                .withProcedureName(PR_ATUALIZAR_UTENTE);

        call.execute(new MapSqlParameterSource()
                .addValue("p_id_utente", idUtente)
                .addValue("p_nome", nome)
                .addValue("p_apelido", apelido)
                .addValue("p_nick", nick)
        );
    }

    @Override
    public void atualizarFoto(Long idUtente, String fotoUrl) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_UTENTE)
                .withProcedureName(PR_ATUALIZAR_FOTO);

        call.execute(new MapSqlParameterSource()
                .addValue("p_id_utente", idUtente)
                .addValue("p_foto_url", fotoUrl)
        );
    }

    @Override
    public void eliminarUtente(Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_UTENTE)
                .withProcedureName(PR_ELIMINAR_UTENTE);

        call.execute(new MapSqlParameterSource()
                .addValue("p_id_utente", idUtente)
        );
    }
}