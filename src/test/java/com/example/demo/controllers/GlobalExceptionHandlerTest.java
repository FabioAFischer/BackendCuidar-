package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.example.demo.exceptions.ResourceNotFoundException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveTratarExcecaoAplicacaoQuandoAppExceptionForLancada() {
        var resposta = handler.tratarExcecaoAplicacao(new ResourceNotFoundException("Idoso", 20L));

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
        assertEquals("RESOURCE_NOT_FOUND", resposta.getBody().get("code"));
        assertNotNull(resposta.getBody().get("timestamp"));
    }

    @Test
    void deveTratarMetodoHttpNaoSuportadoQuandoMetodoForInvalido() {
        var resposta = handler.tratarMetodoHttpNaoSuportado(new HttpRequestMethodNotSupportedException("PATCH"));

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, resposta.getStatusCode());
        assertEquals("METHOD_NOT_ALLOWED", resposta.getBody().get("code"));
    }

    @Test
    void deveTratarErroGenericoQuandoExceptionNaoMapeadaForLancada() {
        var resposta = handler.tratarErroGenerico(new RuntimeException("falha"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        assertEquals("INTERNAL_ERROR", resposta.getBody().get("code"));
    }
}
