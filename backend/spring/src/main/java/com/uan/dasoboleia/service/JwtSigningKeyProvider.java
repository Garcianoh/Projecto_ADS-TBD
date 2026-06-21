package com.uan.dasoboleia.service;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

/**
 * Responsável exclusivamente por construir a SecretKey usada para assinar/verificar tokens JWT.
 */
public class JwtSigningKeyProvider {
    
    private JwtSigningKeyProvider() {
        // classe utilitária, não deve ser instanciada
    }

    static SecretKey from(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
