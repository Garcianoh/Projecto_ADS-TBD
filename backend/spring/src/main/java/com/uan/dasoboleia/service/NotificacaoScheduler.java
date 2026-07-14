package com.uan.dasoboleia.service;

import com.uan.dasoboleia.dto.BoleiaNotificacaoData;
import com.uan.dasoboleia.dto.InscritoNotificacaoData;
import com.uan.dasoboleia.repository.BoleiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduler responsável por verificar boleias próximas e enviar notificações.
 * Corre a cada minuto.
 */
@Component
@RequiredArgsConstructor
public class NotificacaoScheduler {

    private static final int MINUTOS_AVISO = 15;

    private final BoleiaRepository boleiaRepository;
    private final EmailService emailService;

    @Scheduled(fixedDelay = 60000) // corre a cada 60 segundos
    public void notificarInscritosDeBoeiasProximas() {
        List<BoleiaNotificacaoData> boleias =
                boleiaRepository.buscarBoleiasParaNotificarInicio(MINUTOS_AVISO);

        for (BoleiaNotificacaoData boleia : boleias) {
            List<InscritoNotificacaoData> inscritos =
                    boleiaRepository.buscarInscritosParaNotificar(boleia.getIdBoleia());

            for (InscritoNotificacaoData inscrito : inscritos) {
                emailService.enviarNotificacaoBoleiaProxima(
                        inscrito.getEmail(),
                        inscrito.getNome(),
                        boleia.getOrigem(),
                        boleia.getDestino(),
                        boleia.getDataInicio().toString()
                );
            }
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void notificarCriadorSemCondutor() {
        List<BoleiaNotificacaoData> boleias =
                boleiaRepository.buscarBoleiasSemCondutorParaNotificar(MINUTOS_AVISO);

        for (BoleiaNotificacaoData boleia : boleias) {
            if (boleia.getEmailCriador() != null) {
                emailService.enviarNotificacaoSemCondutor(
                        boleia.getEmailCriador(),
                        boleia.getNomeCriador(),
                        boleia.getOrigem(),
                        boleia.getDestino(),
                        boleia.getDataInicio().toString()
                );
            }
        }
    }
}