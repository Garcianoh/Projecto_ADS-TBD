package com.uan.dasoboleia.service;

import com.uan.dasoboleia.dto.RegistarRequest;
import com.uan.dasoboleia.exception.CursoObrigatorioException;
import com.uan.dasoboleia.exception.EmailJaExisteException;
import com.uan.dasoboleia.exception.NickJaExisteException;
import com.uan.dasoboleia.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Responsável exclusivamente por validar as regras de negócio
 * de um pedido de registo, antes de ser persistido.
 */
@Component
@RequiredArgsConstructor
public class RegistarRequestValidator {

    private static final String CATEGORIA_ALUNO = "Aluno";

    private final AuthRepository authRepository;

    public void validar(RegistarRequest request) {
        validarEmailUnico(request.getEmail());
        validarNickUnico(request.getNick());
        validarCursoObrigatorioParaAluno(request);
    }

    private void validarEmailUnico(String email) {
        if (authRepository.emailExiste(email)) {
            throw new EmailJaExisteException(email);
        }
    }

    private void validarNickUnico(String nick) {
        if (authRepository.nickExiste(nick)) {
            throw new NickJaExisteException(nick);
        }
    }

    private void validarCursoObrigatorioParaAluno(RegistarRequest request) {
        boolean isAluno = CATEGORIA_ALUNO.equalsIgnoreCase(request.getCategoria());
        boolean cursoVazio = !StringUtils.hasText(request.getCurso());

        if (isAluno && cursoVazio) {
            throw new CursoObrigatorioException();
        }
    }
}