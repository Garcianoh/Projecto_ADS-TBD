package com.uan.dasoboleia.config;

import com.uan.dasoboleia.service.JwtTokenParser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenResolver implements AuthenticationTokenResolver {

    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtTokenParser jwtTokenParser;

    @Override
    public Authentication resolve(String token) {
        String nick = jwtTokenParser.extrairNick(token);
        String categoria = jwtTokenParser.extrairCategoria(token);
        Long idUtente = jwtTokenParser.extrairIdUtente(token);

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(ROLE_PREFIX + categoria.toUpperCase())
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(nick, null, authorities);

        authentication.setDetails(idUtente);

        return authentication;
    }
}