package com.uan.dasoboleia.service;

import com.uan.dasoboleia.dto.AtualizarBoleiaRequest;
import com.uan.dasoboleia.dto.BoleiaResponse;
import com.uan.dasoboleia.dto.CriarBoleiaRequest;
import com.uan.dasoboleia.dto.InscricaoBoleiaRequest;
import com.uan.dasoboleia.dto.ListaBoleiaResponse;
import com.uan.dasoboleia.exception.BoleiaNotFoundException;
import com.uan.dasoboleia.exception.BoleiaOperacaoNaoPermitidaException;
import com.uan.dasoboleia.repository.BoleiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Orquestra as operações de gestão de boleias.
 */
@Service
@RequiredArgsConstructor
public class BoleiaService {

    private final BoleiaRepository boleiaRepository;

    public BoleiaResponse criar(CriarBoleiaRequest request, Long idUtente) {
        validarConductorTemViatura(request.getTipoUtente(), request.getIdViatura());

        Long idBoleia = boleiaRepository.criarBoleia(
                request.getCusto(),
                request.getDataInicio(),
                request.getTipoBoleia(),
                request.getIdTrajeto(),
                idUtente,
                request.getTipoUtente(),
                request.getIdViatura(),
                request.getDataFim()
        );

        return buscarPorId(idBoleia);
    }

    public BoleiaResponse atualizar(Long idBoleia, AtualizarBoleiaRequest request, Long idUtente) {
        boleiaRepository.atualizarBoleia(
                idBoleia,
                idUtente,
                request.getCusto(),
                request.getDataInicio(),
                request.getTipoBoleia(),
                request.getIdTrajeto(),
                request.getDataFim()
        );
        return buscarPorId(idBoleia);
    }

    public void eliminar(Long idBoleia, Long idUtente) {
        buscarPorId(idBoleia);
        boleiaRepository.eliminarBoleia(idBoleia, idUtente);
    }

    public BoleiaResponse buscarPorId(Long idBoleia) {
        return boleiaRepository.consultarBoleia(idBoleia)
                .orElseThrow(() -> new BoleiaNotFoundException(idBoleia));
    }

    public BoleiaResponse inscrever(Long idBoleia, InscricaoBoleiaRequest request, Long idUtente) {
        validarConductorTemViatura(request.getTipoUtente(), request.getIdViatura());
        boleiaRepository.inscreverNaBoleia(
                idBoleia,
                idUtente,
                request.getTipoUtente(),
                request.getIdViatura()
        );
        return buscarPorId(idBoleia);
    }

    public void cancelarInscricao(Long idBoleia, Long idUtente) {
        boleiaRepository.cancelarInscricao(idBoleia, idUtente);
    }

    public ListaBoleiaResponse listarDisponiveis(Integer pagina) {
        return boleiaRepository.listarDisponiveis(pagina);
    }

    public ListaBoleiaResponse listarIndisponiveis(Integer pagina) {
        return boleiaRepository.listarIndisponiveis(pagina);
    }

    public ListaBoleiaResponse listarPorTrajeto(Long idOrigem, Long idDestino, Integer pagina) {
        return boleiaRepository.listarPorTrajeto(idOrigem, idDestino, pagina);
    }

    public ListaBoleiaResponse listarPorData(LocalDate data, Integer pagina) {
        return boleiaRepository.listarPorData(data, pagina);
    }

    public BoleiaResponse iniciarViagem(Long idBoleia, Long idUtente) {
        boleiaRepository.iniciarViagem(idBoleia, idUtente);
        return buscarPorId(idBoleia);
    }

    public BoleiaResponse concluirViagem(Long idBoleia, Long idUtente) {
        boleiaRepository.concluirViagem(idBoleia, idUtente);
        return buscarPorId(idBoleia);
    }

    public boolean validarCondutor(Long idBoleia, Long idUtente) {
        return boleiaRepository.eCondutorDaBoleia(idBoleia, idUtente);
    }

    private void validarConductorTemViatura(String tipoUtente, Long idViatura) {
        if ("CONDUTOR".equals(tipoUtente) && idViatura == null) {
            throw new BoleiaOperacaoNaoPermitidaException(
                    "É necessário indicar a viatura para se inscrever como condutor.");
        }
    }
}