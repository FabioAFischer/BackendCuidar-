package com.example.demo.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.services.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@RequestBody Map<String, String> dados) {
        return ResponseEntity.ok(authService.autenticarUsuario(dados));
    }

    @PostMapping("/idoso/login")
    public ResponseEntity<?> autenticarIdoso(@RequestBody Map<String, String> dados) {
        return ResponseEntity.ok(authService.autenticarIdoso(dados));
    }

    @PostMapping("/verificar-2fa")
    public ResponseEntity<?> validarCodigoDoisFatores(@RequestBody Map<String, String> dados) {
        String identificador = obterPrimeiroValorPreenchido(dados, "identificador", "cpfCnpj", "cpf", "cnpj");
        String codigo = dados.get("codigo");
        String perfil = dados.get("perfil");

        if (identificador == null || codigo == null || perfil == null) {
            throw new InvalidRequestException("Identificador, perfil e codigo sao obrigatorios");
        }

        return ResponseEntity.ok(authService.validarCodigoDoisFatores(identificador, codigo, perfil));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> solicitarRecuperacaoSenha(@RequestBody Map<String, String> dados) {
        String identificador = dados.get("identificador");
        return ResponseEntity.ok(authService.solicitarRecuperacaoSenha(identificador));
    }

    @PostMapping("/verificar-recuperacao")
    public ResponseEntity<?> validarCodigoRecuperacaoSenha(@RequestBody Map<String, String> dados) {
        String email = dados.get("email");
        String codigo = dados.get("codigo");

        if (email == null || codigo == null) {
            throw new InvalidRequestException("Email e codigo sao obrigatorios");
        }

        return ResponseEntity.ok(authService.validarCodigoRecuperacaoSenha(email, codigo));
    }

    @PostMapping("/nova-senha")
    public ResponseEntity<?> atualizarSenhaRecuperada(@RequestBody Map<String, String> dados) {
        String email = dados.get("email");
        String novaSenha = dados.get("novaSenha");

        if (email == null || novaSenha == null) {
            throw new InvalidRequestException("Email e nova senha sao obrigatorios");
        }

        authService.atualizarSenhaRecuperada(email, novaSenha);
        return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso"));
    }

    private String obterPrimeiroValorPreenchido(Map<String, String> dados, String... chaves) {
        for (String chave : chaves) {
            String valor = dados.get(chave);
            if (valor != null && !valor.isBlank()) return valor;
        }
        return null;
    }
    
    @PostMapping("/reenviar-codigo")
    public ResponseEntity<?> reenviarCodigoDoisFatores(@RequestBody Map<String, String> dados) {
        String identificador = obterPrimeiroValorPreenchido(dados, "identificador", "cpfCnpj", "cpf", "cnpj");
        String perfil = dados.get("perfil");

        if (identificador == null || perfil == null) {
            throw new InvalidRequestException("Identificador e perfil são obrigatórios");
        }

        return ResponseEntity.ok(authService.reenviarCodigoDoisFatores(identificador, perfil));
    }
}
