package com.uan.dasoboleia.config;

import org.springframework.security.core.Authentication;

//Responsável por resolver um token JWT em um objeto Authentication
//que o Spring Security entende, sem consultar o banco de dados.
public interface AuthenticationTokenResolver {
    /**
     * Constrói um Authentication a partir de um token JWT já validado.
     *
     * @param token o token JWT (sem o prefixo "Bearer ")
     * @return o Authentication correspondente
     */
    Authentication resolve(String token);
}
