package com.uan.dasoboleia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Responsável exclusivamente por validar tokens JWT (expiração + correspondência de email).
 */
@Component
@RequiredArgsConstructor
public class JwtTokenValidator {
    private final JwtTokenParser jwtTokenParser;

    public boolean isValido(String token, String nick) {
        String nickExtraido = jwtTokenParser.extrairNick(token);
        return nickExtraido.equals(nick) && !isExpirado(token);
    }

    private boolean isExpirado(String token) {
        Date expiracao = jwtTokenParser.extrairExpiracao(token);
        return expiracao.before(new Date());
    }
}
