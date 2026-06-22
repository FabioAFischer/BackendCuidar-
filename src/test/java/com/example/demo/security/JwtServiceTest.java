package com.example.demo.security;

import static com.example.demo.support.TestDataFactory.administrador;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.demo.entity.Administrador;
import com.example.demo.enums.Perfil;
import com.example.demo.exceptions.InvalidTokenException;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String SECRET = "12345678901234567890123456789012";

    @InjectMocks
    private JwtService service;

    @BeforeEach
    void configurarJwt() {
        ReflectionTestUtils.setField(service, "secret", SECRET);
        ReflectionTestUtils.setField(service, "expirationMs", 60000L);
    }

    @Test
    void deveGerarTokenQuandoUsuarioForValido() {
        String token = service.gerarTokenJwt(administrador());

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void deveExtrairUsuarioIdQuandoTokenForValido() {
        Administrador administrador = administrador();
        String token = service.gerarTokenJwt(administrador);

        assertEquals(1, service.extrairUsuarioId(token));
    }

    @Test
    void deveExtrairPerfilQuandoTokenForValido() {
        String token = service.gerarTokenJwt(administrador());

        assertEquals(Perfil.ADMINISTRADOR, service.extrairPerfil(token));
    }

    @Test
    void deveRetornarVerdadeiroQuandoTokenForValido() {
        String token = service.gerarTokenJwt(administrador());

        assertTrue(service.verificarTokenValido(token));
    }

    @Test
    void deveRetornarFalsoQuandoTokenForInvalido() {
        assertFalse(service.verificarTokenValido("token-invalido"));
    }

    @Test
    void deveLancarExcecaoQuandoTokenForInvalido() {
        assertThrows(InvalidTokenException.class, () -> service.validarTokenJwt("token-invalido"));
    }

    @Test
    void deveLancarExcecaoQuandoTokenEstiverExpirado() {
        ReflectionTestUtils.setField(service, "expirationMs", -1000L);
        String token = service.gerarTokenJwt(administrador());

        assertThrows(InvalidTokenException.class, () -> service.validarTokenJwt(token));
    }
}
