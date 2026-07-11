package com.uan.dasoboleia.repository;

import java.util.List;
import java.util.Optional;

import com.uan.dasoboleia.dto.ViaturaResponse;

//contrato de acesso aos dados de viaturas
//toda a implementação delega para PL/SQL (pkg_viatura).
public interface ViaturaRepository {

    boolean matriculaExiste(String matricula);

    Long registarViatura(String nome, String modelo, String matricula,
                        Integer capacidade, Long idUtente);

    void atualizarViatura(Long idViatura, Long idUtente, String nome,
                        String modelo, String matricula, Integer capacidade);

    List<ViaturaResponse> listarViaturas(Long idUtente);

    Optional<ViaturaResponse> consultarViatura(Long idViatura, Long idUtente);

    void eliminarViatura(Long idViatura, Long idUtente);
}
