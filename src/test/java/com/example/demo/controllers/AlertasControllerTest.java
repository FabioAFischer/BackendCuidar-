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

import com.example.demo.dtos.AlertasDTO;
import com.example.demo.enums.StatusAlertas;
import com.example.demo.services.AlertasService;

@ExtendWith(MockitoExtension.class)
class AlertasControllerTest {

    @Mock
    private AlertasService service;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AlertasController controller;

    @Test
    void deveListarAlertasQuandoCuidadorEstiverAutenticado() {
        Pageable pageable = PageRequest.of(0, 10);
        when(authentication.getPrincipal()).thenReturn(2);
        when(service.listarAlertasAtivosDoCuidador(2, pageable)).thenReturn(new PageImpl<>(List.of(dto()), pageable, 1));

        var resposta = controller.listarAlertas(pageable, authentication);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarAlertasAtivosDoCuidador(2, pageable);
    }

    @Test
    void deveRetornarCreatedQuandoCriarAlerta() {
        AlertasDTO dto = dto();
        when(authentication.getPrincipal()).thenReturn(2);
        when(service.criarAlerta(dto, 2)).thenReturn(dto);

        var resposta = controller.criarAlerta(dto, authentication);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(dto, resposta.getBody());
        verify(service).criarAlerta(dto, 2);
    }

    @Test
    void deveRetornarOkQuandoConfirmarAlerta() {
        AlertasDTO dto = dto();
        dto.setStatusAlertas(StatusAlertas.REALIZADO);
        when(authentication.getPrincipal()).thenReturn(20);
        when(service.confirmarAlerta(1, 20)).thenReturn(dto);

        var resposta = controller.confirmarAlerta(1, authentication);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(StatusAlertas.REALIZADO, resposta.getBody().getStatusAlertas());
        verify(service).confirmarAlerta(1, 20);
    }

    @Test
    void deveRetornarNoContentQuandoCancelarAlerta() {
        when(authentication.getPrincipal()).thenReturn(2);

        var resposta = controller.cancelarAlerta(1, authentication);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).cancelarAlerta(1, 2);
    }

    private AlertasDTO dto() {
        AlertasDTO dto = new AlertasDTO();
        dto.setId(1);
        dto.setIdosoId(20);
        dto.setStatusAlertas(StatusAlertas.AGENDADO);
        return dto;
    }
}
