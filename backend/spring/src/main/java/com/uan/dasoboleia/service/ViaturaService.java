package com.uan.dasoboleia.service;


import com.uan.dasoboleia.dto.AtualizarViaturaRequest;
import com.uan.dasoboleia.dto.RegistarViaturaRequest;
import com.uan.dasoboleia.dto.ViaturaResponse;
import com.uan.dasoboleia.exception.ViaturaNotFoundException;
import com.uan.dasoboleia.repository.ViaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//Responsavel pelas operações de gestão de viaturas

@Service
@RequiredArgsConstructor
public class ViaturaService {
    
    private final ViaturaRepository viaturaRepository;
    private final ViaturaRequestValidator viaturaRequestValidator;

    public ViaturaResponse registar(RegistarViaturaRequest request, Long idUtente) {
        viaturaRequestValidator.validarNovaMatricula(request.getMatricula());

        Long idViatura = viaturaRepository.registarViatura(
                request.getNome(),
                request.getModelo(),
                request.getMatricula(),
                request.getCapacidade(),
                idUtente
        );

        return buscarPorId(idViatura, idUtente);
    }

    public ViaturaResponse atualizar(Long idViatura, AtualizarViaturaRequest request, Long idUtente) {
        viaturaRepository.atualizarViatura(
                idViatura,
                idUtente,
                request.getNome(),
                request.getModelo(),
                request.getMatricula(),
                request.getCapacidade()
        );

        return buscarPorId(idViatura, idUtente);
    }

    public List<ViaturaResponse> listar(Long idUtente) {
        return viaturaRepository.listarViaturas(idUtente);
    }

    public ViaturaResponse buscarPorId(Long idViatura, Long idUtente) {
        return viaturaRepository.consultarViatura(idViatura, idUtente)
                .orElseThrow(() -> new ViaturaNotFoundException(idViatura));
    }

    public void eliminar(Long idViatura, Long idUtente) {
        buscarPorId(idViatura, idUtente);
        viaturaRepository.eliminarViatura(idViatura, idUtente);
    }
}
