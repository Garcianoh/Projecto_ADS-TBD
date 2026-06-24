package com.uan.dasoboleia.config;

import com.uan.dasoboleia.service.JwtTokenParser;
import com.uan.dasoboleia.service.JwtTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenParser jwtTokenParser;
    private final JwtTokenValidator jwtTokenValidator;
    private final AuthenticationTokenResolver authenticationTokenResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        extrairToken(request)
                .filter(this::isContextoAindaNaoAutenticado)
                .ifPresent(this::autenticar);

        filterChain.doFilter(request, response);
    }

    private Optional<String> extrairToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }

        return Optional.of(header.substring(BEARER_PREFIX.length()));
    }

    private boolean isContextoAindaNaoAutenticado(String token) {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void autenticar(String token) {
        try {
            String nick = jwtTokenParser.extrairNick(token);

            if (jwtTokenValidator.isValido(token, nick)) {
                Authentication authentication = authenticationTokenResolver.resolve(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Token inválido/malformado/expirado -> contexto permanece não autenticado
        }
    }
}