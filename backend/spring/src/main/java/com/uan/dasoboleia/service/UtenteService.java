package com.uan.dasoboleia.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uan.dasoboleia.dto.AtualizarPerfilRequest;
import com.uan.dasoboleia.dto.MeuPerfilResponse;
import com.uan.dasoboleia.dto.PerfilPublicoResponse;
import com.uan.dasoboleia.exception.UtenteNaoEncontradoException;
import com.uan.dasoboleia.repository.UtenteRepository;

import lombok.RequiredArgsConstructor;

//Classe que orquestra asoperações de gestão do perfil do utente
@Service
@RequiredArgsConstructor
public class UtenteService {
    
    private final UtenteRepository utenteRepository;
    private final StorageService storageService;

    public PerfilPublicoResponse buscarPorId(Long idUtente) {
        return utenteRepository.buscarPorId(idUtente)
                .orElseThrow(() -> new UtenteNaoEncontradoException(idUtente));
    }

    public MeuPerfilResponse buscarMeuPerfil(Long idutente) {
        return utenteRepository.buscarMeuPerfil(idutente)
                .orElseThrow(() -> new UtenteNaoEncontradoException(idutente));
    }

    public MeuPerfilResponse atualizarPerfil(Long idUtente, AtualizarPerfilRequest request) {
        utenteRepository.atualizarPerfil(
            idUtente,
            request.getNome(),
            request.getApelido(),
            request.getNick()
        );
        return buscarMeuPerfil(idUtente);
    }

    public MeuPerfilResponse atualizarFoto(Long idUtente, MultipartFile ficheiro) {
        String fotoUrl = storageService.guardar(ficheiro, idUtente);
        utenteRepository.atualizarFoto(idUtente, fotoUrl);
        return buscarMeuPerfil(idUtente);
    }

    public void eliminarUtente(Long idUtente) {
        buscarMeuPerfil(idUtente); //para garantir que o utente existe antes da eliminação
        utenteRepository.eliminarUtente(idUtente);
    }
}
