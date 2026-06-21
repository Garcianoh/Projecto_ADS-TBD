package com.uan.dasoboleia.service;

import com.uan.dasoboleia.dto.AuthResponse;
import com.uan.dasoboleia.dto.LoginRequest;
import com.uan.dasoboleia.dto.RegistarRequest;
import com.uan.dasoboleia.dto.UtenteLoginData;
import com.uan.dasoboleia.exception.CredenciaisInvalidasException;
import com.uan.dasoboleia.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public AuthResponse registar(RegistarRequest request) {
        registarRequestValidator.validar(request);

        String passwordHash = passwordEncoder.encode(request.getPassword());

        Long idUtente = authRepository.registarUtente(
                request.getNome(),
                request.getApelido(),
                request.getNumeroUtente(),
                request.getEmail(),
                passwordHash,
                request.getCategoria(),
                request.getCurso()
        );

        String token = jwtTokenGenerator.gerar(idUtente, request.getEmail(), request.getCategoria());

        return new AuthResponse(token, request.getEmail(), request.getCategoria());
    }

    public AuthResponse login(LoginRequest request) {
        UtenteLoginData dadosUtente = buscarUtenteOuFalhar(request.getEmail());

        validarPassword(request.getPassword(), dadosUtente.getPasswordHash());

        String token = jwtTokenGenerator.gerar(
                dadosUtente.getIdUtente(),
                request.getEmail(),
                dadosUtente.getCategoria()
        );

        return new AuthResponse(token, request.getEmail(), dadosUtente.getCategoria());
    }

    private UtenteLoginData buscarUtenteOuFalhar(String email) {
        Optional<UtenteLoginData> dadosUtente = authRepository.buscarParaLogin(email);
        return dadosUtente.orElseThrow(CredenciaisInvalidasException::new);
    }

    private void validarPassword(String passwordTextoPuro, String passwordHash) {
        if (!passwordEncoder.matches(passwordTextoPuro, passwordHash)) {
            throw new CredenciaisInvalidasException();
        }
    }
}