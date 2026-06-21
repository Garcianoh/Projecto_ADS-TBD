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
        String email = jwtTokenParser.extrairEmail(token);
        String categoria = jwtTokenParser.extrairCategoria(token);

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(ROLE_PREFIX + categoria.toUpperCase())
        );

        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }
}