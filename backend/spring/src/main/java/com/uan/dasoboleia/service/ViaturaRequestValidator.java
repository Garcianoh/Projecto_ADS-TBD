package com.uan.dasoboleia.service;

import org.springframework.stereotype.Component;

import com.uan.dasoboleia.exception.MatriculaJaExisteException;
import com.uan.dasoboleia.repository.ViaturaRepository;

import lombok.RequiredArgsConstructor;

//Responsavel por validar toda regra de negocio antes de registar uma viatura
@Component
@RequiredArgsConstructor
public class ViaturaRequestValidator {
    
    private final ViaturaRepository viaturaRepository;

    public void validarNovaMatricula(String matricula) {
        if (viaturaRepository.matriculaExiste(matricula)) {
            throw new MatriculaJaExisteException(matricula);
        }
    }

    public void validarMatriculaParaAtualizacao(String matricula, long idViatura) {
    }
}
