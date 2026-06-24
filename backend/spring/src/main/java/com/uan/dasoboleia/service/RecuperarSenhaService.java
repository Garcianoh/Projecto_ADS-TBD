package com.uan.dasoboleia.service;

import com.uan.dasoboleia.dto.CodigoRecuperacaoData;
import com.uan.dasoboleia.dto.ConfirmarRecuperacaoRequest;
import com.uan.dasoboleia.dto.SolicitarRecuperacaoRequest;
import com.uan.dasoboleia.exception.CodigoInvalidoException;
import com.uan.dasoboleia.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Orquestra o fluxo de recuperação de senha: solicitação do código
 * e confirmação com redefinição da password.
 */
@Service
@RequiredArgsConstructor
public class RecuperarSenhaService {

    private static final String MENSAGEM_GENERICA_SOLICITACAO =
            "Se o email estiver registado, enviámos um código de recuperação.";

    private static final String MENSAGEM_SUCESSO_CONFIRMACAO =
            "Password redefinida com sucesso.";

    private final AuthRepository authRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public String solicitarRecuperacao(SolicitarRecuperacaoRequest request) {
        Optional<CodigoRecuperacaoData> dadosCodigo = authRepository.gerarCodigoRecuperacao(request.getEmail());

        dadosCodigo.ifPresent(dados ->
                emailService.enviarCodigoRecuperacao(request.getEmail(), dados.getNome(), dados.getCodigo())
        );

        // Resposta genérica sempre igual, independente de o email existir ou não
        return MENSAGEM_GENERICA_SOLICITACAO;
    }

    public String confirmarRecuperacao(ConfirmarRecuperacaoRequest request) {
        validarCodigo(request.getEmail(), request.getCodigo());

        String novoPasswordHash = passwordEncoder.encode(request.getNovaPassword());
        authRepository.redefinirPassword(request.getEmail(), novoPasswordHash);

        return MENSAGEM_SUCESSO_CONFIRMACAO;
    }

    private void validarCodigo(String email, String codigo) {
        boolean codigoValido = authRepository.validarCodigoRecuperacao(email, codigo);

        if (!codigoValido) {
            throw new CodigoInvalidoException();
        }
    }
}