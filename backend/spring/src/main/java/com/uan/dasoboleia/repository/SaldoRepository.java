package com.uan.dasoboleia.repository;

import java.math.BigDecimal;

import com.uan.dasoboleia.dto.CarregamentoReferenciaResponse;

//contrato de acesso aos dados de saldo e pagamentos.
public interface SaldoRepository {

    BigDecimal consultarSaldo(Long idUtente);

    CarregamentoReferenciaResponse solicitarCarregamentoReferencia(Long idUtente, BigDecimal valor);

    Long carregarDirecto(Long idUtente, BigDecimal valor);

    void confirmarPagamentoWebhook(String referencia, BigDecimal valor, String estado);

    boolean debitarSaldo(Long idUtente, BigDecimal valor);
}

