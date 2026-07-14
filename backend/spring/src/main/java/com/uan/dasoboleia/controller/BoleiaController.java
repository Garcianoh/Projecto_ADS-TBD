package com.uan.dasoboleia.controller;

import com.uan.dasoboleia.dto.AtualizarBoleiaRequest;
import com.uan.dasoboleia.dto.BoleiaResponse;
import com.uan.dasoboleia.dto.CriarBoleiaRequest;
import com.uan.dasoboleia.dto.InscricaoBoleiaRequest;
import com.uan.dasoboleia.dto.ListaBoleiaResponse;
import com.uan.dasoboleia.dto.MensagemResponse;
import com.uan.dasoboleia.service.BoleiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/boleia")
@RequiredArgsConstructor
public class BoleiaController {

    private final BoleiaService boleiaService;

    @PostMapping
    public ResponseEntity<BoleiaResponse> criar(
            @Valid @RequestBody CriarBoleiaRequest request,
            Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boleiaService.criar(request, idUtente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoleiaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarBoleiaRequest request,
            Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(boleiaService.atualizar(id, request, idUtente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemResponse> eliminar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        boleiaService.eliminar(id, idUtente);
        return ResponseEntity.ok(new MensagemResponse("Boleia eliminada com sucesso."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoleiaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(boleiaService.buscarPorId(id));
    }

    @PostMapping("/{id}/inscrever")
    public ResponseEntity<BoleiaResponse> inscrever(
            @PathVariable Long id,
            @Valid @RequestBody InscricaoBoleiaRequest request,
            Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(boleiaService.inscrever(id, request, idUtente));
    }

    @DeleteMapping("/{id}/cancelar-inscricao")
    public ResponseEntity<MensagemResponse> cancelarInscricao(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        boleiaService.cancelarInscricao(id, idUtente);
        return ResponseEntity.ok(new MensagemResponse("Inscrição cancelada com sucesso."));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<ListaBoleiaResponse> listarDisponiveis(
            @RequestParam(defaultValue = "0") Integer pagina
    ) {
        return ResponseEntity.ok(boleiaService.listarDisponiveis(pagina));
    }

    @GetMapping("/indisponiveis")
    public ResponseEntity<ListaBoleiaResponse> listarIndisponiveis(
            @RequestParam(defaultValue = "0") Integer pagina
    ) {
        return ResponseEntity.ok(boleiaService.listarIndisponiveis(pagina));
    }

    @GetMapping("/por-trajeto")
    public ResponseEntity<ListaBoleiaResponse> listarPorTrajeto(
            @RequestParam Long idOrigem,
            @RequestParam Long idDestino,
            @RequestParam(defaultValue = "0") Integer pagina
    ) {
        return ResponseEntity.ok(boleiaService.listarPorTrajeto(idOrigem, idDestino, pagina));
    }

    @GetMapping("/por-data")
    public ResponseEntity<ListaBoleiaResponse> listarPorData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(defaultValue = "0") Integer pagina
    ) {
        return ResponseEntity.ok(boleiaService.listarPorData(data, pagina));
    }

    @PostMapping("/{id}/iniciar-viagem")
    public ResponseEntity<BoleiaResponse> iniciarViagem(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(boleiaService.iniciarViagem(id, idUtente));
    }

    @PostMapping("/{id}/concluir-viagem")
    public ResponseEntity<BoleiaResponse> concluirViagem(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(boleiaService.concluirViagem(id, idUtente));
    }

    private Long extrairIdUtente(Authentication authentication) {
        return (Long) authentication.getDetails();
    }
}