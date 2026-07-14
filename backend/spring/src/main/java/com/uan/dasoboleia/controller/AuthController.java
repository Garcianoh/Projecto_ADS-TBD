package com.uan.dasoboleia.controller;

import com.uan.dasoboleia.dto.AuthResponse;
import com.uan.dasoboleia.dto.LoginRequest;
import com.uan.dasoboleia.dto.RegistarRequest;
import com.uan.dasoboleia.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;


    @PostMapping("/registar")
    public ResponseEntity<AuthResponse> registar(@Valid @RequestBody RegistarRequest request) {
        AuthResponse response = authService.registar(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Registo com foto (multipart/form-data)
    @PostMapping(value = "/registar-com-foto", consumes = {"multipart/form-data"})
    public ResponseEntity<AuthResponse> registarComFoto(
            @Valid @ModelAttribute RegistarRequest request,
            @RequestParam(value = "foto", required = false) MultipartFile foto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registar(request, foto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
