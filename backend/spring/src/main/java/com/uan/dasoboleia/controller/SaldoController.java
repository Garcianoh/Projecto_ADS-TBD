package com.uan.dasoboleia.controller;

import com.uan.dasoboleia.dto.CarregamentoReferenciaResponse;
import com.uan.dasoboleia.dto.CarregarDirectoRequest;
import com.uan.dasoboleia.dto.ConsultarSaldoResponse;
import com.uan.dasoboleia.dto.MensagemResponse;
import com.uan.dasoboleia.dto.SimularPagamentoRequest;
import com.uan.dasoboleia.dto.SolicitarCarregamentoRequest;
import com.uan.dasoboleia.dto.WebhookPagamentoRequest;
import com.uan.dasoboleia.service.SaldoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saldo")
@RequiredArgsConstructor
public class SaldoController {

    private final SaldoService saldoService;

    @GetMapping
    public ResponseEntity<ConsultarSaldoResponse> consultarSaldo(Authentication authentication) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(saldoService.consultarSaldo(idUtente));
    }

    @PostMapping("/solicitar-carregamento")
    public ResponseEntity<CarregamentoReferenciaResponse> solicitarCarregamento(
            @Valid @RequestBody SolicitarCarregamentoRequest request,
            Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(
                saldoService.solicitarCarregamentoReferencia(idUtente, request.getValor())
        );
    }

    @PostMapping("/carregar-directo")
    public ResponseEntity<ConsultarSaldoResponse> carregarDirecto(
            @Valid @RequestBody CarregarDirectoRequest request,
            Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(saldoService.carregarDirecto(idUtente, request.getValor()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<MensagemResponse> webhook(
            @Valid @RequestBody WebhookPagamentoRequest request
    ) {
        saldoService.processarWebhook(request);
        return ResponseEntity.ok(new MensagemResponse("Pagamento processado com sucesso."));
    }

    @PostMapping("/simular-pagamento")
    public ResponseEntity<MensagemResponse> simularPagamento(
            @Valid @RequestBody SimularPagamentoRequest request
    ) {
        saldoService.simularPagamento(
                request.getReferencia(),
                request.getValor(),
                request.getEstado()
        );
        return ResponseEntity.ok(new MensagemResponse("Pagamento simulado com sucesso."));
    }

    private Long extrairIdUtente(Authentication authentication) {
        return (Long) authentication.getDetails();
    }
}