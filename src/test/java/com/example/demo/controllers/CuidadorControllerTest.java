package com.example.demo.controllers;

import static com.example.demo.support.TestDataFactory.contatoDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.demo.dtos.CuidadorDTO;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.services.CuidadorService;

@ExtendWith(MockitoExtension.class)
class CuidadorControllerTest {

    @Mock
    private CuidadorService service;

    @InjectMocks
    private CuidadorController controller;

    @Test
    void deveListarTodosCuidadoresAtivosSemPerfilInstituicao() {
        var pageable = PageRequest.of(0, 10);
        var pagina = new PageImpl<>(List.of(criarCuidadorDTO()));

        when(service.listarCuidadoresAtivos(pageable)).thenReturn(pagina);

        var resposta = controller.listarCuidadores(null, null, pageable);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarCuidadoresAtivos(pageable);
    }

    @Test
    void deveListarCuidadoresDaInstituicaoQuandoPerfilInstituicao() {
        var pageable = PageRequest.of(0, 10);
        var pagina = new PageImpl<>(List.of(criarCuidadorDTO()));
        var authentication = new UsernamePasswordAuthenticationToken(
                10,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_INSTITUICAO")));

        when(service.listarCuidadoresAtivosPorInstituicao(10, "123.456.789-01", pageable)).thenReturn(pagina);

        var resposta = controller.listarCuidadores("123.456.789-01", authentication, pageable);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarCuidadoresAtivosPorInstituicao(10, "123.456.789-01", pageable);
    }

    @Test
    void deveRetornarOkAoBuscarCuidadorExistente() {
        CuidadorDTO dto = criarCuidadorDTO();

        when(service.buscarCuidadorPorId(2)).thenReturn(dto);

        var resposta = controller.buscarCuidadorPorId(2);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(dto, resposta.getBody());
    }

    @Test
    void deveLancarResourceNotFoundAoBuscarCuidadorInexistente() {
        when(service.buscarCuidadorPorId(99)).thenThrow(new ResourceNotFoundException("Cuidador", 99L));

        assertThrows(ResourceNotFoundException.class, () -> controller.buscarCuidadorPorId(99));
    }

    @Test
    void deveRetornarCreatedAoCadastrarCuidadorValido() {
        CuidadorDTO dto = criarCuidadorDTO();
        CuidadorDTO criado = criarCuidadorDTO();
        criado.setId(2);

        when(service.cadastrarCuidador(dto)).thenReturn(criado);

        var resposta = controller.cadastrarCuidador(dto);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(2, resposta.getBody().getId());
        verify(service).cadastrarCuidador(dto);
    }

    @Test
    void deveLancarDuplicateResourceAoCadastrarCpfDuplicado() {
        CuidadorDTO dto = criarCuidadorDTO();

        when(service.cadastrarCuidador(dto)).thenThrow(new DuplicateResourceException("CPF ja esta em uso"));

        assertThrows(DuplicateResourceException.class, () -> controller.cadastrarCuidador(dto));
    }

    @Test
    void deveRetornarOkAoAtualizarCuidadorValido() {
        CuidadorDTO dto = criarCuidadorDTO();
        dto.setNome("Cuidador Atualizado");

        when(service.atualizarCuidador(2, dto)).thenReturn(dto);

        var resposta = controller.atualizarCuidador(2, dto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Cuidador Atualizado", resposta.getBody().getNome());
        verify(service).atualizarCuidador(2, dto);
    }

    @Test
    void deveLancarResourceNotFoundAoAtualizarCuidadorInexistente() {
        CuidadorDTO dto = criarCuidadorDTO();

        when(service.atualizarCuidador(99, dto)).thenThrow(new ResourceNotFoundException("Cuidador", 99L));

        assertThrows(ResourceNotFoundException.class, () -> controller.atualizarCuidador(99, dto));
    }

    @Test
    void deveRetornarOkAoReativarCuidadorValido() {
        CuidadorDTO dto = criarCuidadorDTO();
        dto.setStatus(Status.ATIVO);

        when(service.reativarCuidador(2, dto)).thenReturn(dto);

        var resposta = controller.reativarCuidador(2, dto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(Status.ATIVO, resposta.getBody().getStatus());
        verify(service).reativarCuidador(2, dto);
    }

    @Test
    void deveRetornarOkAoReativarCuidadorSemDto() {
        CuidadorDTO reativado = criarCuidadorDTO();
        reativado.setStatus(Status.ATIVO);

        when(service.reativarCuidador(2, null)).thenReturn(reativado);

        var resposta = controller.reativarCuidador(2, null);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(Status.ATIVO, resposta.getBody().getStatus());
    }

    @Test
    void deveLancarResourceNotFoundAoReativarCuidadorInexistente() {
        when(service.reativarCuidador(99, null)).thenThrow(new ResourceNotFoundException("Cuidador", 99L));

        assertThrows(ResourceNotFoundException.class, () -> controller.reativarCuidador(99, null));
    }

    @Test
    void deveRetornarNoContentAoInativarCuidadorExistente() {
        var resposta = controller.inativarCuidador(2);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).inativarCuidador(2);
    }

    @Test
    void deveLancarResourceNotFoundAoInativarCuidadorInexistente() {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Cuidador", 99L))
                .when(service).inativarCuidador(99);

        assertThrows(ResourceNotFoundException.class, () -> controller.inativarCuidador(99));
    }

    @Test
    void deveRetornarPongAoResponderPing() {
        assertEquals("pong", controller.responderPingCuidador());
    }

    private CuidadorDTO criarCuidadorDTO() {
        CuidadorDTO dto = new CuidadorDTO();
        dto.setId(2);
        dto.setNome("Cuidador");
        dto.setCpf("12345678901");
        dto.setEmail("cuidador@email.com");
        dto.setSenha("Senha@123");
        dto.setStatus(Status.ATIVO);
        dto.setInstituicaoId(10);
        dto.setContato(contatoDTO());
        return dto;
    }
}
