package com.uan.dasoboleia.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Responsável exclusivamente por ler/extrair informação de um token JWT já existente.
 */
@Component
public class JwtTokenParser {
    
    private static final String CLAIM_ID_UTENTE = "id_utente";
    private static final String CLAIM_CATEGORIA = "categoria";
    private static final String CLAIM_EMAIL = "email";

    @Value("${jwt.secret}")
    private String secret;

    public String extrairNick(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public Long extrairIdUtente(String token) {
        return extrairTodasClaims(token).get(CLAIM_ID_UTENTE, Long.class);
    }

    public String extrairCategoria(String token) {
        return extrairTodasClaims(token).get(CLAIM_CATEGORIA, String.class);
    }

    public String extrairEmail(String token) {
        return extrairTodasClaims(token).get(CLAIM_EMAIL, String.class);
    }

    public Date extrairExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    private <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extrairTodasClaims(token));
    }

    private Claims extrairTodasClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return JwtSigningKeyProvider.from(secret);
    }
}
