package com.uan.dasoboleia.repository;

import com.uan.dasoboleia.dto.BoleiaResponse;
import com.uan.dasoboleia.dto.ListaBoleiaResponse;
import com.uan.dasoboleia.dto.InscritoNotificacaoData;
import com.uan.dasoboleia.dto.BoleiaNotificacaoData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acesso aos dados de boleias.
 * Toda a implementação delega para PL/SQL (pkg_boleia).
 */
public interface BoleiaRepository {

    Long criarBoleia(BigDecimal custo, LocalDate dataInicio, String tipoBoleia,
                     Long idTrajeto, Long idUtente, String tipoUtente,
                     Long idViatura, LocalDate dataFim);

    void atualizarBoleia(Long idBoleia, Long idUtente, BigDecimal custo,
                         LocalDate dataInicio, String tipoBoleia,
                         Long idTrajeto, LocalDate dataFim);

    void eliminarBoleia(Long idBoleia, Long idUtente);

    Optional<BoleiaResponse> consultarBoleia(Long idBoleia);

    void inscreverNaBoleia(Long idBoleia, Long idUtente,
                           String tipoUtente, Long idViatura);

    void cancelarInscricao(Long idBoleia, Long idUtente);

    ListaBoleiaResponse listarDisponiveis(Integer pagina);

    ListaBoleiaResponse listarIndisponiveis(Integer pagina);

    ListaBoleiaResponse listarPorTrajeto(Long idOrigem, Long idDestino, Integer pagina);

    ListaBoleiaResponse listarPorData(LocalDate data, Integer pagina);

    void iniciarViagem(Long idBoleia, Long idUtente);

    void concluirViagem(Long idBoleia, Long idUtente);

    boolean eCondutorDaBoleia(Long idBoleia, Long idUtente);

    List<BoleiaNotificacaoData> buscarBoleiasParaNotificarInicio(Integer minutos);

    List<BoleiaNotificacaoData> buscarBoleiasSemCondutorParaNotificar(Integer minutos);

    List<InscritoNotificacaoData> buscarInscritosParaNotificar(Long idBoleia);
}