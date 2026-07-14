package com.uan.dasoboleia.repository;

import com.uan.dasoboleia.dto.BoleiaNotificacaoData;
import com.uan.dasoboleia.dto.BoleiaResponse;
import com.uan.dasoboleia.dto.InscritoNotificacaoData;
import com.uan.dasoboleia.dto.ListaBoleiaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcBoleiaRepository implements BoleiaRepository {

    private static final String PACKAGE_BOLEIA = "PKG_BOLEIA";
    private static final int ITENS_POR_PAGINA = 10;

    private final DataSource dataSource;

    @Override
    public Long criarBoleia(BigDecimal custo, LocalDate dataInicio, String tipoBoleia,
                            Long idTrajeto, Long idUtente, String tipoUtente,
                            Long idViatura, LocalDate dataFim) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_CRIAR_BOLEIA")
                .declareParameters(
                        new SqlOutParameter("p_id_boleia_out", Types.NUMERIC)
                );

        Map<String, Object> resultado = call.execute(new MapSqlParameterSource()
                .addValue("p_custo", custo)
                .addValue("p_data_inicio", dataInicio != null ? Date.valueOf(dataInicio) : null)
                .addValue("p_tipo_boleia", tipoBoleia)
                .addValue("p_id_trajeto", idTrajeto)
                .addValue("p_id_utente", idUtente)
                .addValue("p_tipo_utente", tipoUtente)
                .addValue("p_id_viatura", idViatura)
                .addValue("p_data_fim", dataFim != null ? Date.valueOf(dataFim) : null)
        );

        return ((Number) resultado.get("p_id_boleia_out")).longValue();
    }

    @Override
    public void atualizarBoleia(Long idBoleia, Long idUtente, BigDecimal custo,
                                LocalDate dataInicio, String tipoBoleia,
                                Long idTrajeto, LocalDate dataFim) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_ATUALIZAR_BOLEIA");

        call.execute(new MapSqlParameterSource()
                .addValue("p_id_boleia", idBoleia)
                .addValue("p_id_utente", idUtente)
                .addValue("p_custo", custo)
                .addValue("p_data_inicio", dataInicio != null ? Date.valueOf(dataInicio) : null)
                .addValue("p_tipo_boleia", tipoBoleia)
                .addValue("p_id_trajeto", idTrajeto)
                .addValue("p_data_fim", dataFim != null ? Date.valueOf(dataFim) : null)
        );
    }

    @Override
    public void eliminarBoleia(Long idBoleia, Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_ELIMINAR_BOLEIA");

        call.execute(new MapSqlParameterSource()
                .addValue("p_id_boleia", idBoleia)
                .addValue("p_id_utente", idUtente)
        );
    }

    @Override
    public Optional<BoleiaResponse> consultarBoleia(Long idBoleia) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_CONSULTAR_BOLEIA")
                .declareParameters(
                        new SqlOutParameter("p_custo_out", Types.NUMERIC),
                        new SqlOutParameter("p_data_inicio_out", Types.DATE),
                        new SqlOutParameter("p_tipo_boleia_out", Types.VARCHAR),
                        new SqlOutParameter("p_estado_out", Types.VARCHAR),
                        new SqlOutParameter("p_id_trajeto_out", Types.NUMERIC),
                        new SqlOutParameter("p_origem_out", Types.VARCHAR),
                        new SqlOutParameter("p_destino_out", Types.VARCHAR),
                        new SqlOutParameter("p_total_inscritos_out", Types.NUMERIC),
                        new SqlOutParameter("p_capacidade_out", Types.NUMERIC),
                        new SqlOutParameter("p_timestamp_out", Types.TIMESTAMP)
                );

        Map<String, Object> r = call.execute(
                new MapSqlParameterSource().addValue("p_id_boleia", idBoleia)
        );

        if (r.get("p_custo_out") == null) {
            return Optional.empty();
        }

        Number capacidade = (Number) r.get("p_capacidade_out");
        Number totalInscritos = (Number) r.get("p_total_inscritos_out");
        Date dataInicio = (Date) r.get("p_data_inicio_out");
        Timestamp ts = (Timestamp) r.get("p_timestamp_out");

        return Optional.of(new BoleiaResponse(
                idBoleia,
                new BigDecimal(r.get("p_custo_out").toString()),
                dataInicio != null ? dataInicio.toLocalDate() : null,
                (String) r.get("p_tipo_boleia_out"),
                (String) r.get("p_estado_out"),
                ((Number) r.get("p_id_trajeto_out")).longValue(),
                (String) r.get("p_origem_out"),
                (String) r.get("p_destino_out"),
                totalInscritos != null ? totalInscritos.intValue() : 0,
                capacidade != null ? capacidade.intValue() : null,
                ts != null ? ts.toLocalDateTime() : null
        ));
    }

    @Override
    public void inscreverNaBoleia(Long idBoleia, Long idUtente,
                                  String tipoUtente, Long idViatura) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_INSCREVER_NA_BOLEIA");

        call.execute(new MapSqlParameterSource()
                .addValue("p_id_boleia", idBoleia)
                .addValue("p_id_utente", idUtente)
                .addValue("p_tipo_utente", tipoUtente)
                .addValue("p_id_viatura", idViatura)
        );
    }

    @Override
    public void cancelarInscricao(Long idBoleia, Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_CANCELAR_INSCRICAO");

        call.execute(new MapSqlParameterSource()
                .addValue("p_id_boleia", idBoleia)
                .addValue("p_id_utente", idUtente)
        );
    }

    @Override
    public ListaBoleiaResponse listarDisponiveis(Integer pagina) {
        return listar("DISPONIVEIS", null, null, null, pagina);
    }

    @Override
    public ListaBoleiaResponse listarIndisponiveis(Integer pagina) {
        return listar("INDISPONIVEIS", null, null, null, pagina);
    }

    @Override
    public ListaBoleiaResponse listarPorTrajeto(Long idOrigem, Long idDestino, Integer pagina) {
        return listar("POR_TRAJETO", idOrigem, idDestino, null, pagina);
    }

    @Override
    public ListaBoleiaResponse listarPorData(LocalDate data, Integer pagina) {
        return listar("POR_DATA", null, null, data, pagina);
    }

    private ListaBoleiaResponse listar(String filtro, Long idOrigem,
                                       Long idDestino, LocalDate data, Integer pagina) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_LISTAR_BOLEIAS")
                .declareParameters(
                        new SqlOutParameter("p_cursor_out", Types.REF_CURSOR,
                                (rs, rowNum) -> mapearBoleiaLista(rs)),
                        new SqlOutParameter("p_total_out", Types.NUMERIC)
                );

        Map<String, Object> resultado = call.execute(new MapSqlParameterSource()
                .addValue("p_filtro", filtro)
                .addValue("p_id_origem", idOrigem)
                .addValue("p_id_destino", idDestino)
                .addValue("p_data", data != null ? Date.valueOf(data) : null)
                .addValue("p_pagina", pagina)
        );

        List<BoleiaResponse> boleias = (List<BoleiaResponse>) resultado.get("p_cursor_out");
        Number total = (Number) resultado.get("p_total_out");
        long totalRegistos = total != null ? total.longValue() : 0;
        int totalPaginas = (int) Math.ceil((double) totalRegistos / ITENS_POR_PAGINA);

        return new ListaBoleiaResponse(boleias, pagina, totalPaginas, totalRegistos);
    }

    @Override
    public void iniciarViagem(Long idBoleia, Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_INICIAR_VIAGEM");

        call.execute(new MapSqlParameterSource()
                .addValue("p_id_boleia", idBoleia)
                .addValue("p_id_utente", idUtente)
        );
    }

    @Override
    public void concluirViagem(Long idBoleia, Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_CONCLUIR_VIAGEM");

        call.execute(new MapSqlParameterSource()
                .addValue("p_id_boleia", idBoleia)
                .addValue("p_id_utente", idUtente)
        );
    }

    @Override
    public boolean eCondutorDaBoleia(Long idBoleia, Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withFunctionName("FN_E_CONDUTOR_DA_BOLEIA");

        Number resultado = call.executeFunction(Number.class,
                new MapSqlParameterSource()
                        .addValue("p_id_boleia", idBoleia)
                        .addValue("p_id_utente", idUtente)
        );

        return resultado.intValue() == 1;
    }

    @Override
    public List<BoleiaNotificacaoData> buscarBoleiasParaNotificarInicio(Integer minutos) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_BOLEIAS_PARA_NOTIFICAR_INICIO")
                .declareParameters(
                        new SqlOutParameter("p_cursor_out", Types.REF_CURSOR,
                                (rs, rowNum) -> mapearBoleiaNotificacao(rs, false))
                );

        Map<String, Object> resultado = call.execute(
                new MapSqlParameterSource().addValue("p_minutos", minutos)
        );

        return (List<BoleiaNotificacaoData>) resultado.get("p_cursor_out");
    }

    @Override
    public List<BoleiaNotificacaoData> buscarBoleiasSemCondutorParaNotificar(Integer minutos) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_BOLEIAS_SEM_CONDUTOR_PARA_NOTIFICAR")
                .declareParameters(
                        new SqlOutParameter("p_cursor_out", Types.REF_CURSOR,
                                (rs, rowNum) -> mapearBoleiaNotificacao(rs, true))
                );

        Map<String, Object> resultado = call.execute(
                new MapSqlParameterSource().addValue("p_minutos", minutos)
        );

        return (List<BoleiaNotificacaoData>) resultado.get("p_cursor_out");
    }

    @Override
    public List<InscritoNotificacaoData> buscarInscritosParaNotificar(Long idBoleia) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_BOLEIA)
                .withProcedureName("PR_BUSCAR_INSCRITOS_PARA_NOTIFICAR")
                .declareParameters(
                        new SqlOutParameter("p_cursor_out", Types.REF_CURSOR,
                                (rs, rowNum) -> mapearInscrito(rs))
                );

        Map<String, Object> resultado = call.execute(
                new MapSqlParameterSource().addValue("p_id_boleia", idBoleia)
        );

        return (List<InscritoNotificacaoData>) resultado.get("p_cursor_out");
    }

    private BoleiaResponse mapearBoleiaLista(ResultSet rs) throws SQLException {
        Date dataInicio = rs.getDate("data_inicio");
        Timestamp ts = rs.getTimestamp("timestamp");
        Number capacidade = (Number) rs.getObject("capacidade");

        return new BoleiaResponse(
                rs.getLong("id_boleia"),
                rs.getBigDecimal("custo"),
                dataInicio != null ? dataInicio.toLocalDate() : null,
                rs.getString("tipo_boleia"),
                null,
                null,
                rs.getString("origem"),
                rs.getString("destino"),
                rs.getInt("total_inscritos"),
                capacidade != null ? capacidade.intValue() : null,
                ts != null ? ts.toLocalDateTime() : null
        );
    }

    private BoleiaNotificacaoData mapearBoleiaNotificacao(ResultSet rs,
                                                          boolean temCriador) throws SQLException {
        Date dataInicio = rs.getDate("data_inicio");
        String emailCriador = temCriador ? rs.getString("email_criador") : null;
        String nomeCriador = temCriador ? rs.getString("nome_criador") : null;

        return new BoleiaNotificacaoData(
                rs.getLong("id_boleia"),
                dataInicio != null ? dataInicio.toLocalDate() : null,
                rs.getString("origem"),
                rs.getString("destino"),
                emailCriador,
                nomeCriador
        );
    }

    private InscritoNotificacaoData mapearInscrito(ResultSet rs) throws SQLException {
        Date dataInicio = rs.getDate("data_inicio");
        return new InscritoNotificacaoData(
                rs.getString("email"),
                rs.getString("nome"),
                dataInicio != null ? dataInicio.toLocalDate() : null,
                rs.getString("origem"),
                rs.getString("destino")
        );
    }
}