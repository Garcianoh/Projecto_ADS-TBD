package com.uan.dasoboleia.controller;

import com.uan.dasoboleia.dto.AtualizarViaturaRequest;
import com.uan.dasoboleia.dto.MensagemResponse;
import com.uan.dasoboleia.dto.RegistarViaturaRequest;
import com.uan.dasoboleia.dto.ViaturaResponse;
import com.uan.dasoboleia.service.ViaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/viatura")
@RequiredArgsConstructor
public class ViaturaController {
    
    private final ViaturaService viaturaService;

    @PostMapping
    public ResponseEntity<ViaturaResponse>  registar(
        @Valid @RequestBody RegistarViaturaRequest request,
        Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(viaturaService.registar(request, idUtente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViaturaResponse> atualizar(
        @PathVariable Long id,
        @Valid @RequestBody AtualizarViaturaRequest request,
        Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(viaturaService.atualizar(id, request, idUtente));
    }

    @GetMapping
    public ResponseEntity<List<ViaturaResponse>> listar(Authentication authentication) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(viaturaService.listar(idUtente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViaturaResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        return ResponseEntity.ok(viaturaService.buscarPorId(id, idUtente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemResponse> eliminar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long idUtente = extrairIdUtente(authentication);
        viaturaService.eliminar(id, idUtente);
        return ResponseEntity.ok(new MensagemResponse("Viatura eliminada com sucesso."));
    }


    private Long extrairIdUtente(Authentication authentication) {
        return (Long) authentication.getDetails();
    }
}
