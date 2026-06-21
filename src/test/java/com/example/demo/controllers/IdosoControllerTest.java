package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.demo.dtos.IdosoDTO;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.services.IdosoService;

@ExtendWith(MockitoExtension.class)
class IdosoControllerTest {

    @Mock
    private IdosoService service;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private IdosoController controller;

    @Test
    void deveListarIdososDaInstituicaoQuandoPerfilInstituicao() {
        Pageable pageable = PageRequest.of(0, 10);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTITUICAO"))).when(authentication).getAuthorities();
        when(authentication.getPrincipal()).thenReturn(10);
        when(service.listarIdososAtivosPorInstituicao(10, pageable)).thenReturn(new PageImpl<>(List.of(dto()), pageable, 1));

        var resposta = controller.listarIdosos(authentication, pageable);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarIdososAtivosPorInstituicao(10, pageable);
    }

    @Test
    void deveRetornarCreatedQuandoCadastrarIdoso() {
        IdosoDTO dto = dto();
        when(service.cadastrarIdoso(dto)).thenReturn(dto);

        var resposta = controller.cadastrarIdoso(dto);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(dto, resposta.getBody());
        verify(service).cadastrarIdoso(dto);
    }

    @Test
    void deveRetornarSenhaAcessoQuandoCuidadorEstiverAutenticado() {
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_CUIDADOR"))).when(authentication).getAuthorities();
        when(authentication.getPrincipal()).thenReturn(2);
        when(service.obterSenhaAcessoDoIdoso(20, 2, "senha")).thenReturn(Map.of("senha", "BC-ABCDEFGH"));

        var resposta = controller.obterSenhaAcessoDoIdoso(20, Map.of("senha", "senha"), authentication);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("BC-ABCDEFGH", resposta.getBody().get("senha"));
        verify(service).obterSenhaAcessoDoIdoso(20, 2, "senha");
    }

    @Test
    void deveLancarExcecaoQuandoObterSenhaSemPerfilCuidador() {
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_INSTITUICAO"))).when(authentication).getAuthorities();

        assertThrows(UnauthorizedException.class,
                () -> controller.obterSenhaAcessoDoIdoso(20, Map.of("senha", "senha"), authentication));
    }

    @Test
    void deveRetornarNoContentQuandoInativarIdoso() {
        var resposta = controller.inativarIdoso(20);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).inativarIdoso(20);
    }

    private IdosoDTO dto() {
        IdosoDTO dto = new IdosoDTO();
        dto.setId(20);
        dto.setNome("Maria");
        return dto;
    }
}
