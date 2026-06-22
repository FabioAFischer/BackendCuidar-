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

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.services.ContatoService;

@ExtendWith(MockitoExtension.class)
class ContatoControllerTest {

    @Mock
    private ContatoService service;

    @InjectMocks
    private ContatoController controller;

    @Test
    void deveListarContatosQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        when(service.listarContatos(pageable)).thenReturn(new PageImpl<>(List.of(dto()), pageable, 1));

        var resposta = controller.listarContatos(pageable);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarContatos(pageable);
    }

    @Test
    void deveRetornarCreatedQuandoCriarContato() {
        ContatoDTO dto = dto();
        when(service.criarContato(dto)).thenReturn(dto);

        var resposta = controller.criarContato(dto);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(dto, resposta.getBody());
        verify(service).criarContato(dto);
    }

    @Test
    void deveRetornarNoContentQuandoExcluirContato() {
        var resposta = controller.excluirContato(5);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).excluirContato(5);
    }

    private ContatoDTO dto() {
        ContatoDTO dto = new ContatoDTO();
        dto.setId(5);
        dto.setDdd("11");
        dto.setTelefone("999999999");
        return dto;
    }
}
