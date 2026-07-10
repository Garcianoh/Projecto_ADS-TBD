package com.uan.dasoboleia.service;

import org.springframework.web.multipart.MultipartFile;

// interface para serviçoes de armazenamentos de ficheiros.
public interface StorageService {
    
    //guarda o ficheiro e devolve a URL publica de acesso
    String guardar(MultipartFile ficheiro, Long idUtente);

    //elimina um ficheiro pelo seu nome
    void eliminar(String nomeFicheiro);
}
