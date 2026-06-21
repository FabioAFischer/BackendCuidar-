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
import com.example.demo.dtos.VinculoDTO;
import com.example.demo.enums.TipoVinculo;
import com.example.demo.services.VinculoService;

@ExtendWith(MockitoExtension.class)
class VinculoControllerTest {

    @Mock
    private VinculoService service;

    @InjectMocks
    private VinculoController controller;

    @Test
    void deveListarVinculosQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        when(service.listarVinculos(pageable)).thenReturn(new PageImpl<>(List.of(vinculo()), pageable, 1));

        var resposta = controller.listarVinculos(pageable);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarVinculos(pageable);
    }

    @Test
    void deveRetornarCreatedQuandoCriarVinculo() {
        VinculoDTO dto = vinculo();
        when(service.criarVinculo(dto)).thenReturn(dto);

        var resposta = controller.criarVinculo(dto);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(dto, resposta.getBody());
        verify(service).criarVinculo(dto);
    }

    @Test
    void deveBuscarContatoDeEmergenciaQuandoIdosoExistir() {
        ContatoDTO contato = new ContatoDTO();
        contato.setId(5);
        when(service.buscarContatoDeEmergencia(20)).thenReturn(contato);

        var resposta = controller.buscarContatoDeEmergencia(20);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(5, resposta.getBody().getId());
        verify(service).buscarContatoDeEmergencia(20);
    }

    @Test
    void deveRetornarNoContentQuandoExcluirVinculo() {
        var resposta = controller.excluirVinculo(1);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).excluirVinculo(1);
    }

    private VinculoDTO vinculo() {
        VinculoDTO dto = new VinculoDTO();
        dto.setId(1);
        dto.setIdosoId(20);
        dto.setCuidadorId(2);
        dto.setTipoVinculo(TipoVinculo.PADRAO);
        return dto;
    }
}
