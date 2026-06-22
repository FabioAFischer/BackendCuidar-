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

import com.example.demo.dtos.AdministradorDTO;
import com.example.demo.services.AdministradorService;

@ExtendWith(MockitoExtension.class)
class AdministradorControllerTest {

    @Mock
    private AdministradorService service;

    @InjectMocks
    private AdministradorController controller;

    @Test
    void deveListarAdministradoresQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        when(service.listarAdministradoresAtivos(pageable)).thenReturn(new PageImpl<>(List.of(dto()), pageable, 1));

        var resposta = controller.listarAdministradores(pageable);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarAdministradoresAtivos(pageable);
    }

    @Test
    void deveRetornarCreatedQuandoCadastrarAdministrador() {
        AdministradorDTO dto = dto();
        when(service.cadastrarAdministrador(dto)).thenReturn(dto);

        var resposta = controller.cadastrarAdministrador(dto);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(dto, resposta.getBody());
        verify(service).cadastrarAdministrador(dto);
    }

    @Test
    void deveRetornarNoContentQuandoInativarAdministrador() {
        var resposta = controller.inativarAdministrador(1);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).inativarAdministrador(1);
    }

    private AdministradorDTO dto() {
        AdministradorDTO dto = new AdministradorDTO();
        dto.setId(1);
        dto.setNome("Admin");
        return dto;
    }
}
