package com.uan.dasoboleia.repository;

import com.uan.dasoboleia.dto.ViaturaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcViaturaRepository implements ViaturaRepository {

    private static final String PACKAGE_VIATURA = "PKG_VIATURA";

    private static final String FN_MATRICULA_EXISTE = "FN_MATRICULA_EXISTE";
    private static final String PR_REGISTAR_VIATURA = "PR_REGISTAR_VIATURA";
    private static final String PR_ATUALIZAR_VIATURA = "PR_ATUALIZAR_VIATURA";
    private static final String PR_LISTAR_VIATURAS_UTENTE = "PR_LISTAR_VIATURAS_UTENTE";
    private static final String PR_CONSULTAR_VIATURA = "PR_CONSULTAR_VIATURA";
    private static final String PR_ELIMINAR_VIATURA = "PR_ELIMINAR_VIATURA";

    private final DataSource dataSource;

    @Override
    public boolean matriculaExiste(String matricula) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_VIATURA)
                .withFunctionName(FN_MATRICULA_EXISTE);

        Number resultado = call.executeFunction(
                Number.class,
                new MapSqlParameterSource().addValue("p_matricula", matricula)
        );

        return resultado.intValue() == 1;
    }

    @Override
    public Long registarViatura(String nome, String modelo, String matricula,
                                Integer capacidade, Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_VIATURA)
                .withProcedureName(PR_REGISTAR_VIATURA)
                .declareParameters(
                        new SqlOutParameter("p_id_viatura_out", Types.NUMERIC)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_nome", nome)
                .addValue("p_modelo", modelo)
                .addValue("p_matricula", matricula)
                .addValue("p_capacidade", capacidade)
                .addValue("p_id_utente", idUtente);

        Map<String, Object> resultado = call.execute(params);

        return ((Number) resultado.get("p_id_viatura_out")).longValue();
    }

    @Override
    public void atualizarViatura(Long idViatura, Long idUtente, String nome,
                                 String modelo, String matricula, Integer capacidade) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_VIATURA)
                .withProcedureName(PR_ATUALIZAR_VIATURA);

        call.execute(new MapSqlParameterSource()
                .addValue("p_id_viatura", idViatura)
                .addValue("p_id_utente", idUtente)
                .addValue("p_nome", nome)
                .addValue("p_modelo", modelo)
                .addValue("p_matricula", matricula)
                .addValue("p_capacidade", capacidade)
        );
    }

    @Override
    public List<ViaturaResponse> listarViaturas(Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_VIATURA)
                .withProcedureName(PR_LISTAR_VIATURAS_UTENTE)
                .declareParameters(
                        new SqlOutParameter("p_cursor_out", Types.REF_CURSOR,
                                (rs, rowNum) -> mapearViatura(rs))
                );

        Map<String, Object> resultado = call.execute(
                new MapSqlParameterSource().addValue("p_id_utente", idUtente)
        );

        return (List<ViaturaResponse>) resultado.get("p_cursor_out");
    }

    @Override
    public Optional<ViaturaResponse> consultarViatura(Long idViatura, Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_VIATURA)
                .withProcedureName(PR_CONSULTAR_VIATURA)
                .declareParameters(
                        new SqlOutParameter("p_nome_out", Types.VARCHAR),
                        new SqlOutParameter("p_modelo_out", Types.VARCHAR),
                        new SqlOutParameter("p_matricula_out", Types.VARCHAR),
                        new SqlOutParameter("p_capacidade_out", Types.NUMERIC),
                        new SqlOutParameter("p_timestamp_out", Types.TIMESTAMP)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_id_viatura", idViatura)
                .addValue("p_id_utente", idUtente);

        Map<String, Object> resultado = call.execute(params);

        String nome = (String) resultado.get("p_nome_out");

        if (nome == null) {
            return Optional.empty();
        }

        Timestamp ts = (Timestamp) resultado.get("p_timestamp_out");

        return Optional.of(new ViaturaResponse(
                idViatura,
                nome,
                (String) resultado.get("p_modelo_out"),
                (String) resultado.get("p_matricula_out"),
                ((Number) resultado.get("p_capacidade_out")).intValue(),
                ts != null ? ts.toLocalDateTime() : null
        ));
    }

    @Override
    public void eliminarViatura(Long idViatura, Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_VIATURA)
                .withProcedureName(PR_ELIMINAR_VIATURA);

        call.execute(new MapSqlParameterSource()
                .addValue("p_id_viatura", idViatura)
                .addValue("p_id_utente", idUtente)
        );
    }

    private ViaturaResponse mapearViatura(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("timestamp");
        return new ViaturaResponse(
                rs.getLong("id_viatura"),
                rs.getString("nome"),
                rs.getString("modelo"),
                rs.getString("matricula"),
                rs.getInt("capacidade"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}