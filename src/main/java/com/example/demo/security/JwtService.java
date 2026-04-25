package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Administrador;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-ms}")
    private Long expirationMs;

    public String gerarTokenAdministrador(Administrador administrador) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", administrador.getId());
        claims.put("nome", administrador.getNome());
        claims.put("email", administrador.getEmail());
        claims.put("perfil", administrador.getPerfil().name());

        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(administrador.getEmail())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
