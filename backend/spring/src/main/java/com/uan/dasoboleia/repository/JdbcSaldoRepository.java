package com.uan.dasoboleia.repository;

import com.uan.dasoboleia.dto.CarregamentoReferenciaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JdbcSaldoRepository implements SaldoRepository {
    
    private static final String PACKAGE_SALDO = "PKG_SALDO";

    private static final String PR_CONSULTAR_SALDO = "PR_CONSULTAR_SALDO";
    private static final String PR_SOLICITAR_CARREGAMENTO_REFERENCIA = "PR_SOLICITAR_CARREGAMENTO_REFERENCIA";
    private static final String PR_CARREGAR_DIRECTO = "PR_CARREGAR_DIRECTO";
    private static final String PR_CONFIRMAR_PAGAMENTO_WEBHOOK = "PR_CONFIRMAR_PAGAMENTO_WEBHOOK";
    private static final String PR_DEBITAR_SALDO = "PR_DEBITAR_SALDO";

    private final DataSource dataSource;

    @Override
    public BigDecimal consultarSaldo(Long idUtente) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_SALDO)
                .withProcedureName(PR_CONSULTAR_SALDO)
                .declareParameters(
                        new SqlOutParameter("p_saldo_out", Types.NUMERIC),
                        new SqlOutParameter("p_moeda_out", Types.VARCHAR)
                );

        Map<String, Object> resultado = call.execute(
                new MapSqlParameterSource().addValue("p_id_utente", idUtente)
        );

        Number saldo = (Number) resultado.get("p_saldo_out");
        return saldo != null ? new BigDecimal(saldo.toString()) : BigDecimal.ZERO;
    }

    @Override
    public CarregamentoReferenciaResponse solicitarCarregamentoReferencia(Long idUtente, BigDecimal valor) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_SALDO)
                .withProcedureName(PR_SOLICITAR_CARREGAMENTO_REFERENCIA)
                .declareParameters(
                        new SqlOutParameter("p_entidade_out", Types.VARCHAR),
                        new SqlOutParameter("p_referencia_out", Types.VARCHAR),
                        new SqlOutParameter("p_id_transacao_out", Types.NUMERIC)
                );

        Map<String, Object> resultado = call.execute(
                new MapSqlParameterSource()
                        .addValue("p_id_utente", idUtente)
                        .addValue("p_valor", valor)
        );

        Long idTransacao = ((Number) resultado.get("p_id_transacao_out")).longValue();
        String entidade = (String) resultado.get("p_entidade_out");
        String referencia = (String) resultado.get("p_referencia_out");

        return new CarregamentoReferenciaResponse(
                idTransacao,
                entidade,
                referencia,
                valor,
                "AOA",
                "Insira a entidade " + entidade + " e a referência " + referencia +
                " no sistema de pagamento para completar o carregamento de " +
                valor + " AOA."
        );
    }

    @Override
    public Long carregarDirecto(Long idUtente, BigDecimal valor) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_SALDO)
                .withProcedureName(PR_CARREGAR_DIRECTO)
                .declareParameters(
                        new SqlOutParameter("p_id_transacao_out", Types.NUMERIC)
                );

        Map<String, Object> resultado = call.execute(
                new MapSqlParameterSource()
                        .addValue("p_id_utente", idUtente)
                        .addValue("p_valor", valor)
        );

        return ((Number) resultado.get("p_id_transacao_out")).longValue();
    }

    @Override
    public void confirmarPagamentoWebhook(String referencia, BigDecimal valor, String estado) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_SALDO)
                .withProcedureName(PR_CONFIRMAR_PAGAMENTO_WEBHOOK);

        call.execute(new MapSqlParameterSource()
                .addValue("p_referencia", referencia)
                .addValue("p_valor", valor)
                .addValue("p_estado", estado)
        );
    }

    @Override
    public boolean debitarSaldo(Long idUtente, BigDecimal valor) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_SALDO)
                .withProcedureName(PR_DEBITAR_SALDO)
                .declareParameters(
                        new SqlOutParameter("p_sucesso_out", Types.NUMERIC)
                );

        Map<String, Object> resultado = call.execute(
                new MapSqlParameterSource()
                        .addValue("p_id_utente", idUtente)
                        .addValue("p_valor", valor)
        );

        Number sucesso = (Number) resultado.get("p_sucesso_out");
        return sucesso != null && sucesso.intValue() == 1;
    }
}
