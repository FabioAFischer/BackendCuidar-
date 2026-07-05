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

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.services.InstituicaoService;

@ExtendWith(MockitoExtension.class)
class InstituicaoControllerTest {

    @Mock
    private InstituicaoService service;

    @InjectMocks
    private InstituicaoController controller;

    @Test
    void deveListarInstituicoesQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        when(service.listarInstituicoesAtivas(null, pageable)).thenReturn(new PageImpl<>(List.of(dto()), pageable, 1));

        var resposta = controller.listarInstituicoes(null, pageable);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarInstituicoesAtivas(null, pageable);
    }

    @Test
    void deveRetornarCreatedQuandoCadastrarInstituicao() {
        InstituicaoDTO dto = dto();
        when(service.cadastrarInstituicao(dto)).thenReturn(dto);

        var resposta = controller.cadastrarInstituicao(dto);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(dto, resposta.getBody());
        verify(service).cadastrarInstituicao(dto);
    }

    @Test
    void deveRetornarNoContentQuandoReativarInstituicao() {
        var resposta = controller.reativarInstituicao(3);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).reativarInstituicao(3);
    }

    private InstituicaoDTO dto() {
        InstituicaoDTO dto = new InstituicaoDTO();
        dto.setId(3);
        dto.setNome("Instituicao");
        return dto;
    }
}
