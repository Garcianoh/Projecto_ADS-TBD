package com.uan.dasoboleia.controller;

import com.uan.dasoboleia.dto.AtualizarPerfilRequest;
import com.uan.dasoboleia.dto.MeuPerfilResponse;
import com.uan.dasoboleia.dto.MensagemResponse;
import com.uan.dasoboleia.dto.PerfilPublicoResponse;
import com.uan.dasoboleia.service.UtenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/utente")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;

    @GetMapping("/{id}")
    public ResponseEntity<PerfilPublicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(utenteService.buscarPorId(id));
    }

    @GetMapping("/me")
    public ResponseEntity<MeuPerfilResponse> meuPerfil(Authentication authentication) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(utenteService.buscarMeuPerfil(idUtente));
    }

    @PutMapping("/me")
    public ResponseEntity<MeuPerfilResponse> atualizarPerfil(
            Authentication authentication,
            @Valid @RequestBody AtualizarPerfilRequest request
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(utenteService.atualizarPerfil(idUtente, request));
    }

    @PutMapping("/me/foto")
    public ResponseEntity<MeuPerfilResponse> atualizarFoto(
            Authentication authentication,
            @RequestParam("foto") MultipartFile foto
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(utenteService.atualizarFoto(idUtente, foto));
    }

    @DeleteMapping("/me")
    public ResponseEntity<MensagemResponse> eliminarConta(Authentication authentication) {
        Long idUtente = extrairIdUtente(authentication);
        utenteService.eliminarUtente(idUtente);
        return ResponseEntity.ok(new MensagemResponse("Conta eliminada com sucesso."));
    }

    private Long extrairIdUtente(Authentication authentication) {
        return (Long) authentication.getDetails();
    }
}