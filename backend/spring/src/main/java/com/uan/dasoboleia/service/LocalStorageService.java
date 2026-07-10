package com.uan.dasoboleia.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import com.uan.dasoboleia.exception.FotoInvalidaException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

//implementaçao so StorageService
@Service
public class LocalStorageService implements StorageService {
    
    private static final List<String> TIPOS_PERMITIDOS = List.of(
        "image/jpeg", "image/png", "image/webp"
    );

    //equivalente a 5MB
    private static final long TAMANHO_MAXIMO = 5 * 1024 * 1024;

    @Value("${storage.upload-dir:/app/uploads/fotos}")
    private String uploadDir;

    @Value("${storage.base-url:http://localhost:8080/uploads/fotos}")
    private String baseUrl;

    @Override
    public String guardar(MultipartFile ficheiro, Long idUtente) {
        validarFicheiro(ficheiro);

        String extensao = obterExtensao(ficheiro.getOriginalFilename());
        String nomeFicheiro = "utente_" +idUtente + "_" + UUID.randomUUID() + extensao;

        try {
            Path diretorio = Paths.get(uploadDir);
            Files.createDirectories(diretorio);
            Files.copy(ficheiro.getInputStream(), diretorio.resolve(nomeFicheiro));
        } catch (IOException e) {
            throw new RuntimeException("Falha ao guardar a foto de perfil", e);
        }

        return baseUrl + "/" + nomeFicheiro;
    }

    @Override
    public void eliminar(String nomeFicheiro) {
        try {
            Path caminho = Paths.get(uploadDir).resolve(nomeFicheiro);
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao elimar o ficheiro", e);
        }
    }

    private void validarFicheiro(MultipartFile ficheiro) {
        if (ficheiro == null || ficheiro.isEmpty()) {
            throw new FotoInvalidaException("O ficheiro de foto não pode estar vazio.");
        }

        if (!TIPOS_PERMITIDOS.contains(ficheiro.getContentType())) {
            throw new FotoInvalidaException("Tipo de ficheiro inválido. Apenas JPEG, ONG e WebP são permitidos.");
        }

        if (ficheiro.getSize() > TAMANHO_MAXIMO) {
            throw new FotoInvalidaException("O ficheiro excede o tamanho máximo permitido de 5MB.");
        }
    }

    private String obterExtensao(String nomeOriginal) {
        if (nomeOriginal == null || !nomeOriginal.contains(".")) {
            return ".jpg";
        }
        return nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
    }
}
