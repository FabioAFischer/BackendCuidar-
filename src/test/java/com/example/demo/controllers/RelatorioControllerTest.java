package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import com.example.demo.dtos.RelatorioDTO;
import com.example.demo.dtos.RelatorioDTO.RelatorioInstituicaoDTO;
import com.example.demo.services.RelatorioService;

@ExtendWith(MockitoExtension.class)
class RelatorioControllerTest {

    @Mock
    private RelatorioService service;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private RelatorioController controller;

    @Test
    void deveGerarRelatorioGeralQuandoSolicitado() {
        RelatorioDTO relatorio = new RelatorioDTO();
        when(service.gerarRelatorioGeral()).thenReturn(relatorio);

        var resposta = controller.gerarRelatorioGeral();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(relatorio, resposta.getBody());
        verify(service).gerarRelatorioGeral();
    }

    @Test
    void deveGerarRelatorioInstituicaoQuandoInstituicaoEstiverAutenticada() {
        RelatorioInstituicaoDTO relatorio = new RelatorioInstituicaoDTO();
        when(authentication.getPrincipal()).thenReturn(10);
        when(service.gerarRelatorioInstituicao(10)).thenReturn(relatorio);

        var resposta = controller.gerarRelatorioInstituicao(authentication);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(relatorio, resposta.getBody());
        verify(service).gerarRelatorioInstituicao(10);
    }
}
