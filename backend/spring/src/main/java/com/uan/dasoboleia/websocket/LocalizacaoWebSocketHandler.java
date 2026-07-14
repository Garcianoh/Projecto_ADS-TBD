package com.uan.dasoboleia.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uan.dasoboleia.dto.LocalizacaoMessage;
import com.uan.dasoboleia.service.BoleiaService;
import com.uan.dasoboleia.service.JwtTokenParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler WebSocket para partilha de localização em tempo real.
 * O condutor envia coordenadas → servidor distribui para todos os inscritos na boleia.
 */
@Component
@RequiredArgsConstructor
public class LocalizacaoWebSocketHandler extends TextWebSocketHandler {

    private final BoleiaService boleiaService;
    private final JwtTokenParser jwtTokenParser;
    private final ObjectMapper objectMapper;

    // Mapa: idBoleia → conjunto de sessões activas
    private final Map<Long, Set<WebSocketSession>> sessoesPorBoleia = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long idBoleia = extrairIdBoleia(session);
        sessoesPorBoleia.computeIfAbsent(idBoleia, k -> ConcurrentHashMap.newKeySet())
                .add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long idBoleia = extrairIdBoleia(session);
        Set<WebSocketSession> sessoes = sessoesPorBoleia.get(idBoleia);
        if (sessoes != null) {
            sessoes.remove(session);
            if (sessoes.isEmpty()) {
                sessoesPorBoleia.remove(idBoleia);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Long idBoleia = extrairIdBoleia(session);
        Long idUtente = extrairIdUtente(session);

        // Só o condutor pode enviar localização
        if (!boleiaService.validarCondutor(idBoleia, idUtente)) {
            session.sendMessage(new TextMessage(
                    "{\"erro\": \"Só o condutor pode partilhar a localização.\"}"
            ));
            return;
        }

        LocalizacaoMessage localizacao = objectMapper.readValue(
                message.getPayload(), LocalizacaoMessage.class
        );
        localizacao.setIdBoleia(idBoleia);

        String payload = objectMapper.writeValueAsString(localizacao);

        // Distribui para todos os inscritos (incluindo o próprio condutor)
        Set<WebSocketSession> sessoes = sessoesPorBoleia.get(idBoleia);
        if (sessoes != null) {
            for (WebSocketSession s : sessoes) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(payload));
                }
            }
        }
    }

    private Long extrairIdBoleia(WebSocketSession session) {
        String uri = session.getUri().toString();
        String[] partes = uri.split("/");
        return Long.parseLong(partes[partes.length - 2]);
    }

    private Long extrairIdUtente(WebSocketSession session) {
        String token = session.getUri().getQuery();
        if (token != null && token.startsWith("token=")) {
            token = token.substring(6);
            return jwtTokenParser.extrairIdUtente(token);
        }
        return null;
    }
}