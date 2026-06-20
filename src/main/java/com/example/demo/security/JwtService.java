package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Usuario;
import com.example.demo.enums.Perfil;
import com.example.demo.exceptions.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public String gerarTokenJwt(Usuario usuario) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());
        claims.put("nome", usuario.getNome());
        claims.put("perfil", usuario.getPerfil().name());

        return Jwts.builder()
                .extrairClaims(claims)
                .subject(String.valueOf(usuario.getId()))
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(gerarChaveAssinatura())
                .compact();
    }

    public boolean verificarTokenValido(String token) {
        try {
            validarTokenJwt(token);
            return true;
        } catch (InvalidTokenException exception) {
            return false;
        }
    }

    public void validarTokenJwt(String token) {
        try {
            extrairClaims(token);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidTokenException();
        }
    }

    public Integer extrairUsuarioId(String token) {
        return Integer.valueOf(extrairClaims(token).getSubject());
    }

    public Perfil extrairPerfil(String token) {
        return Perfil.valueOf(extrairClaims(token).get("perfil", String.class));
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(gerarChaveAssinatura())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey gerarChaveAssinatura() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
