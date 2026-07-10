package com.uan.dasoboleia.repository;

import java.util.Optional;

import com.uan.dasoboleia.dto.MeuPerfilResponse;
import com.uan.dasoboleia.dto.PerfilPublicoResponse;

//permite o acesso ao pacote Utente no PL/SQL(pkg_utente)
public interface UtenteRepository {


    Optional<PerfilPublicoResponse> buscarPorId(Long idUtente);

    Optional<MeuPerfilResponse> buscarMeuPerfil(Long idUtente);

    void atualizarPerfil(Long idUtente, String nome, String apelido, String nick);

    void atualizarFoto(Long idUtente, String fotoUrl);

    void eliminarUtente(Long idUtente);
}
