package com.example.demo.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.LoginRequestDTO;
import com.example.demo.dtos.LoginResponseDTO;
import com.example.demo.security.AuthException;
import com.example.demo.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
        summary = "Login do administrador",
        description = "Autentica um administrador ativo e retorna um token JWT Bearer"
    )
    @PostMapping("/login/administrador")
    public ResponseEntity<?> loginAdministrador(@RequestBody LoginRequestDTO dto) {
        try {
            LoginResponseDTO response = authService.loginAdministrador(dto);
            return ResponseEntity.ok(response);
        } catch (AuthException ex) {
            return ResponseEntity.status(ex.getStatus()).body(Map.of("mensagem", ex.getMessage()));
        }
    }
}
