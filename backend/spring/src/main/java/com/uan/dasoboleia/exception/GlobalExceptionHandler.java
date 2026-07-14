package com.uan.dasoboleia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EmailJaExisteException.class)
    public ResponseEntity<Map<String, Object>> tratarEmailJaExistente(EmailJaExisteException ex) {
        return construirResposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CursoObrigatorioException.class)
    public ResponseEntity<Map<String, Object>> tratarCursoObrigatorio(CursoObrigatorioException ex) {
        return construirResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CategoriaInvalidaException.class)
    public ResponseEntity<Map<String, Object>> tratarCategoriaInvalida(CategoriaInvalidaException ex) {
        return construirResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Map<String, Object>> tratarCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        return construirResposta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(NickJaExisteException.class)
    public ResponseEntity<Map<String, Object>> tratarNickJaExiste(NickJaExisteException ex) {
        return construirResposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ContaBloqueadaException.class)
    public ResponseEntity<Map<String, Object>> tratarContaBloqueada(ContaBloqueadaException ex) {
        return construirResposta(HttpStatus.LOCKED, ex.getMessage());
    }

    @ExceptionHandler(CodigoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> tratarCodigoInvalido(CodigoInvalidoException ex) {
        return construirResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UtenteNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarUtenteNaoEncontrado(UtenteNaoEncontradoException ex) {
        return construirResposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(FotoInvalidaException.class)
    public ResponseEntity<Map<String, Object>> tratarFotoInvalida(FotoInvalidaException ex) {
        return construirResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarErrosValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();

        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now());
        corpo.put("status", HttpStatus.BAD_REQUEST.value());
        corpo.put("erros", erros);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(MatriculaJaExisteException.class)
    public ResponseEntity<Map<String, Object>> tratarMatriculaJaExiste(MatriculaJaExisteException ex) {
        return construirResposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ViaturaInvalidaException.class)
    public ResponseEntity<Map<String, Object>> tratarViaturaInvalida(ViaturaInvalidaException ex) {
        return construirResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ViaturaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> tratarViaturaNotFound(ViaturaNotFoundException ex) {
        return construirResposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> tratarSaldoInsuficiente(SaldoInsuficienteException ex) {
        return construirResposta(HttpStatus.PAYMENT_REQUIRED, ex.getMessage());
    }

    @ExceptionHandler(PagamentoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> tratarPagamentoInvalido(PagamentoInvalidoException ex) {
        return construirResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    

    private ResponseEntity<Map<String, Object>> construirResposta(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now());
        corpo.put("status", status.value());
        corpo.put("mensagem", mensagem);

        return ResponseEntity.status(status).body(corpo);
    }
}
