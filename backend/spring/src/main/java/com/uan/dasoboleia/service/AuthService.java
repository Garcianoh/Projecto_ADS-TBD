package com.uan.dasoboleia.service;

import com.uan.dasoboleia.dto.AuthResponse;
import com.uan.dasoboleia.dto.LoginRequest;
import com.uan.dasoboleia.dto.RegistarRequest;
import com.uan.dasoboleia.dto.UtenteLoginData;
import com.uan.dasoboleia.exception.ContaBloqueadaException;
import com.uan.dasoboleia.exception.CredenciaisInvalidasException;
import com.uan.dasoboleia.repository.AuthRepository;
import com.uan.dasoboleia.repository.UtenteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Orquestra os fluxos de registo e login de utentes.
 * Não contém regras de validação nem acesso direto ao banco —
 * delega essas responsabilidades a outras classes.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final RegistarRequestValidator registarRequestValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final StorageService storageService;
    private final UtenteRepository utenteRepository;

    public AuthResponse registar(RegistarRequest request, MultipartFile foto) {
        registarRequestValidator.validar(request);

        String passwordHash = passwordEncoder.encode(request.getPassword());

        Long idUtente = authRepository.registarUtente(
                request.getNome(),
                request.getApelido(),
                request.getNick(),
                request.getNumeroUtente(),
                request.getEmail(),
                passwordHash,
                request.getCategoria(),
                request.getCurso()
        );

        // Upload de foto se fornecida
        if (foto != null && !foto.isEmpty()) {
            String fotoUrl = storageService.guardar(foto, idUtente);
            utenteRepository.atualizarFoto(idUtente, fotoUrl);
        }

        String token = jwtTokenGenerator.gerar(
                idUtente,
                request.getNick(),
                request.getCategoria(),
                request.getEmail()
        );

        return new AuthResponse(token, request.getNick(), request.getCategoria());
    }

    /*public AuthResponse registar(RegistarRequest request) {
        registarRequestValidator.validar(request);

        String passwordHash = passwordEncoder.encode(request.getPassword());

        Long idUtente = authRepository.registarUtente(
                request.getNome(),
                request.getApelido(),
                request.getNick(),
                request.getNumeroUtente(),
                request.getEmail(),
                passwordHash,
                request.getCategoria(),
                request.getCurso()
        );

        String token = jwtTokenGenerator.gerar(
                idUtente,
                request.getNick(),
                request.getCategoria(),
                request.getEmail()
        );

        return new AuthResponse(token, request.getNick(), request.getCategoria());
    }*/

    public AuthResponse login(LoginRequest request) {
        UtenteLoginData dadosUtente = buscarUtenteOuFalhar(request.getNick());

        validarContaNaoBloqueada(dadosUtente);
        validarPasswordOuRegistarFalha(request, dadosUtente);

        authRepository.resetarTentativas(dadosUtente.getIdUtente());

        String token = jwtTokenGenerator.gerar(
                dadosUtente.getIdUtente(),
                request.getNick(),
                dadosUtente.getCategoria(),
                dadosUtente.getEmail()
        );

        return new AuthResponse(token, request.getNick(), dadosUtente.getCategoria());
    }

    private UtenteLoginData buscarUtenteOuFalhar(String nick) {
        Optional<UtenteLoginData> dadosUtente = authRepository.buscarParaLogin(nick);
        return dadosUtente.orElseThrow(CredenciaisInvalidasException::new);
    }

    private void validarContaNaoBloqueada(UtenteLoginData dadosUtente) {
        if (dadosUtente.isBloqueado()) {
            throw new ContaBloqueadaException(dadosUtente.getMinutosRestantes());
        }
    }

    private void validarPasswordOuRegistarFalha(LoginRequest request, UtenteLoginData dadosUtente) {
        boolean passwordCorreta = passwordEncoder.matches(request.getPassword(), dadosUtente.getPasswordHash());

        if (!passwordCorreta) {
            authRepository.registarTentativaFalhada(request.getNick());
            throw new CredenciaisInvalidasException();
        }
    }
}