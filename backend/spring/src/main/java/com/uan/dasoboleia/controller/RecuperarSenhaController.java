package com.uan.dasoboleia.controller;

import com.uan.dasoboleia.dto.ConfirmarRecuperacaoRequest;
import com.uan.dasoboleia.dto.MensagemResponse;
import com.uan.dasoboleia.dto.SolicitarRecuperacaoRequest;
import com.uan.dasoboleia.service.RecuperarSenhaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/recuperar-senha")
@RequiredArgsConstructor
public class RecuperarSenhaController {

    private final RecuperarSenhaService recuperarSenhaService;

    @PostMapping("/solicitar")
    public ResponseEntity<MensagemResponse> solicitar(@Valid @RequestBody SolicitarRecuperacaoRequest request) {
        String mensagem = recuperarSenhaService.solicitarRecuperacao(request);
        return ResponseEntity.ok(new MensagemResponse(mensagem));
    }

    @PostMapping("/confirmar")
    public ResponseEntity<MensagemResponse> confirmar(@Valid @RequestBody ConfirmarRecuperacaoRequest request) {
        String mensagem = recuperarSenhaService.confirmarRecuperacao(request);
        return ResponseEntity.ok(new MensagemResponse(mensagem));
    }
}