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

import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.services.PrescricaoService;

@ExtendWith(MockitoExtension.class)
class PrescricaoControllerTest {

    @Mock
    private PrescricaoService service;

    @InjectMocks
    private PrescricaoController controller;

    @Test
    void deveListarPrescricoesQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        when(service.listarPrescricoesAtivas(pageable)).thenReturn(new PageImpl<>(List.of(dto()), pageable, 1));

        var resposta = controller.listarPrescricoes(pageable);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarPrescricoesAtivas(pageable);
    }

    @Test
    void deveRetornarCreatedQuandoCriarPrescricao() {
        PrescricaoDTO dto = dto();
        when(service.criarPrescricao(dto)).thenReturn(dto);

        var resposta = controller.criarPrescricao(dto);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(dto, resposta.getBody());
        verify(service).criarPrescricao(dto);
    }

    @Test
    void deveRetornarNoContentQuandoInativarPrescricao() {
        var resposta = controller.inativarPrescricao(1);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).inativarPrescricao(1);
    }

    private PrescricaoDTO dto() {
        PrescricaoDTO dto = new PrescricaoDTO();
        dto.setId(1);
        return dto;
    }
}
