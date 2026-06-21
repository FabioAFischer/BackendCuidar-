package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.services.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    @Test
    void deveAutenticarUsuarioQuandoDadosForemValidos() {
        Map<String, String> dados = Map.of("identificador", "123", "senha", "senha", "perfil", "ADMINISTRADOR");
        Map<String, Object> retorno = Map.of("autenticado", true);
        when(authService.autenticarUsuario(dados)).thenReturn(retorno);

        var resposta = controller.autenticarUsuario(dados);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(retorno, resposta.getBody());
        verify(authService).autenticarUsuario(dados);
    }

    @Test
    void deveValidarCodigoDoisFatoresQuandoDadosForemValidos() {
        Map<String, String> dados = Map.of("identificador", "123", "codigo", "123456", "perfil", "CUIDADOR");
        Map<String, Object> retorno = Map.of("autenticado", true);
        when(authService.validarCodigoDoisFatores("123", "123456", "CUIDADOR")).thenReturn(retorno);

        var resposta = controller.validarCodigoDoisFatores(dados);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(retorno, resposta.getBody());
        verify(authService).validarCodigoDoisFatores("123", "123456", "CUIDADOR");
    }

    @Test
    void deveLancarExcecaoQuandoValidarCodigoSemCamposObrigatorios() {
        assertThrows(InvalidRequestException.class, () -> controller.validarCodigoDoisFatores(Map.of("codigo", "123456")));
    }

    @Test
    void deveReenviarCodigoQuandoDadosForemValidos() {
        Map<String, String> dados = Map.of("cpf", "123", "perfil", "CUIDADOR");
        Map<String, Object> retorno = Map.of("mensagem", "Código reenviado com sucesso");
        when(authService.reenviarCodigoDoisFatores("123", "CUIDADOR")).thenReturn(retorno);

        var resposta = controller.reenviarCodigoDoisFatores(dados);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(retorno, resposta.getBody());
        verify(authService).reenviarCodigoDoisFatores("123", "CUIDADOR");
    }
}
