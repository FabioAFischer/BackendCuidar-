package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import com.example.demo.dtos.RemedioDTO;
import com.example.demo.services.RemedioService;

@ExtendWith(MockitoExtension.class)
class RemedioControllerTest {

    @Mock
    private RemedioService service;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private RemedioController controller;

    @Test
    void deveListarRemediosQuandoCuidadorEstiverAutenticado() {
        Pageable pageable = PageRequest.of(0, 10);
        when(authentication.getPrincipal()).thenReturn(2);
        when(service.listarRemediosAtivos(2, pageable)).thenReturn(new PageImpl<>(List.of(dto()), pageable, 1));

        var resposta = controller.listarRemedios(pageable, authentication);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarRemediosAtivos(2, pageable);
    }

    @Test
    void deveRetornarCreatedQuandoCriarRemedio() {
        RemedioDTO dto = dto();
        when(authentication.getPrincipal()).thenReturn(2);
        when(service.criarRemedio(dto, 2)).thenReturn(dto);

        var resposta = controller.criarRemedio(dto, authentication);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(dto, resposta.getBody());
        verify(service).criarRemedio(dto, 2);
    }

    @Test
    void deveRetornarNoContentQuandoInativarRemedio() {
        when(authentication.getPrincipal()).thenReturn(2);

        var resposta = controller.inativarRemedio(1, authentication);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).inativarRemedio(1, 2);
    }

    @Test
    void deveRetornarPongQuandoResponderPing() {
        assertEquals("pong", controller.responderPingRemedio());
    }

    private RemedioDTO dto() {
        RemedioDTO dto = new RemedioDTO();
        dto.setId(1);
        dto.setNome("Dipirona");
        return dto;
    }
}
