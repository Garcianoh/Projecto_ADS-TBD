package com.uan.dasoboleia.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uan.dasoboleia.dto.CursoResponse;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/curso")
@AllArgsConstructor
public class CursoController {
    
    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<List<CursoResponse>> listar() {
        List<CursoResponse> cursos = jdbcTemplate.query(
                "SELECT id_curso, nome FROM curso ORDER BY nome",
                (rs, rowNum) -> new CursoResponse(
                        rs.getLong("id_curso"),
                         rs.getString("nome")
                )
        );
        return ResponseEntity.ok(cursos);
    }
}
