package com.uan.dasoboleia.service;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 *  Class Responsável exclusivamente por gerar tokens JWT.
 */
@Component
public class JwtTokenGenerator {
    
    private static final String CLAIM_ID_UTENTE = "id_utente";
    private static final String CLAIM_CATEGORIA = "categoria";
    private static final String CLAIM_EMAIL = "email";


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    public String gerar(Long idUtente, String nick, String categoria, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_ID_UTENTE, idUtente);
        claims.put(CLAIM_CATEGORIA, categoria);
        claims.put(CLAIM_EMAIL, email);

        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(nick)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        return JwtSigningKeyProvider.from(secret);
    }
}
