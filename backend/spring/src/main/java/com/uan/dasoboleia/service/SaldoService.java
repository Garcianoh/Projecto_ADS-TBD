package com.uan.dasoboleia.service;

import com.uan.dasoboleia.dto.CarregamentoReferenciaResponse;
import com.uan.dasoboleia.dto.ConsultarSaldoResponse;
import com.uan.dasoboleia.dto.WebhookPagamentoRequest;
import com.uan.dasoboleia.exception.PagamentoInvalidoException;
import com.uan.dasoboleia.exception.SaldoInsuficienteException;
import com.uan.dasoboleia.repository.SaldoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

//trata das operações de gestão de saldo e pagamentos

@Service
@RequiredArgsConstructor
public class SaldoService {
    
    private static final String MOEDA = "AOA";

    private final SaldoRepository saldoRepository;

    public ConsultarSaldoResponse consultarSaldo(Long idUtente) {
        BigDecimal saldo = saldoRepository.consultarSaldo(idUtente);
        return new ConsultarSaldoResponse(saldo, MOEDA);
    }

    public CarregamentoReferenciaResponse solicitarCarregamentoReferencia(
            Long idUtente, BigDecimal valor) {
        return saldoRepository.solicitarCarregamentoReferencia(idUtente, valor);
    }

    public ConsultarSaldoResponse carregarDirecto(Long idUtente, BigDecimal valor) {
        saldoRepository.carregarDirecto(idUtente, valor);
        return consultarSaldo(idUtente);
    }

    public void processarWebhook(WebhookPagamentoRequest request) {
        try {
            saldoRepository.confirmarPagamentoWebhook(
                    request.getReferencia(),
                    request.getValor(),
                    request.getEstado()
            );
        } catch (Exception e) {
            throw new PagamentoInvalidoException(
                    "Erro ao processar pagamento: " + e.getMessage()
            );
        }
    }

    public void simularPagamento(String referencia, BigDecimal valor, String estado) {
        try {
            saldoRepository.confirmarPagamentoWebhook(referencia, valor, estado);
        } catch (Exception e) {
            throw new PagamentoInvalidoException(
                    "Erro ao simular pagamento: " + e.getMessage()
            );
        }
    }

    //lança saldo insuficienteException se o saldo for insuficiente
    public void debitar(Long idUtente, BigDecimal valor) {
        boolean sucesso = saldoRepository.debitarSaldo(idUtente, valor);
        if (!sucesso) {
            throw new SaldoInsuficienteException();
        }
    }
}
