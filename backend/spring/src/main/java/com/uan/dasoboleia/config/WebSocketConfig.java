package com.uan.dasoboleia.config;

import com.uan.dasoboleia.websocket.LocalizacaoWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final LocalizacaoWebSocketHandler localizacaoWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(localizacaoWebSocketHandler, "/ws/boleia/{idBoleia}/localizacao")
                .setAllowedOrigins("*");
    }
}