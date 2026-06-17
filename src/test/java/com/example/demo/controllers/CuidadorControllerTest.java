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
    void ListarTodos_semPerfilInstituicao_retornaTodosAtivos() {
        var pageable = PageRequest.of(0, 10);
        var pagina = new PageImpl<>(List.of(cuidadorDTO()));

        when(service.listarAtivos(pageable)).thenReturn(pagina);

        var resposta = controller.listarTodos(null, null, pageable);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarAtivos(pageable);
    }

    @Test
    void ListarTodos_comPerfilInstituicao_retornaCuidadoresDaInstituicao() {
        var pageable = PageRequest.of(0, 10);
        var pagina = new PageImpl<>(List.of(cuidadorDTO()));
        var authentication = new UsernamePasswordAuthenticationToken(
                10,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_INSTITUICAO")));

        when(service.listarAtivosPorInstituicao(10, "123.456.789-01", pageable)).thenReturn(pagina);

        var resposta = controller.listarTodos("123.456.789-01", authentication, pageable);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().getTotalElements());
        verify(service).listarAtivosPorInstituicao(10, "123.456.789-01", pageable);
    }

    @Test
    void Buscar_porIdExistente_retornaOk() {
        CuidadorDTO dto = cuidadorDTO();

        when(service.buscarPorId(2)).thenReturn(dto);

        var resposta = controller.buscarPorId(2);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(dto, resposta.getBody());
    }

    @Test
    void Buscar_porIdInexistente_lancaResourceNotFound() {
        when(service.buscarPorId(99)).thenThrow(new ResourceNotFoundException("Cuidador", 99L));

        assertThrows(ResourceNotFoundException.class, () -> controller.buscarPorId(99));
    }

    @Test
    void Criar_dadosValidos_retornaCreated() {
        CuidadorDTO dto = cuidadorDTO();
        CuidadorDTO criado = cuidadorDTO();
        criado.setId(2);

        when(service.criar(dto)).thenReturn(criado);

        var resposta = controller.criar(dto);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(2, resposta.getBody().getId());
        verify(service).criar(dto);
    }

    @Test
    void Criar_cpfDuplicado_lancaDuplicateResource() {
        CuidadorDTO dto = cuidadorDTO();

        when(service.criar(dto)).thenThrow(new DuplicateResourceException("CPF ja esta em uso"));

        assertThrows(DuplicateResourceException.class, () -> controller.criar(dto));
    }

    @Test
    void Atualizar_dadosValidos_retornaOk() {
        CuidadorDTO dto = cuidadorDTO();
        dto.setNome("Cuidador Atualizado");

        when(service.atualizar(2, dto)).thenReturn(dto);

        var resposta = controller.atualizar(2, dto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Cuidador Atualizado", resposta.getBody().getNome());
        verify(service).atualizar(2, dto);
    }

    @Test
    void Atualizar_cuidadorInexistente_lancaResourceNotFound() {
        CuidadorDTO dto = cuidadorDTO();

        when(service.atualizar(99, dto)).thenThrow(new ResourceNotFoundException("Cuidador", 99L));

        assertThrows(ResourceNotFoundException.class, () -> controller.atualizar(99, dto));
    }

    @Test
    void Reativar_dadosValidos_retornaOk() {
        CuidadorDTO dto = cuidadorDTO();
        dto.setStatus(Status.ATIVO);

        when(service.reativar(2, dto)).thenReturn(dto);

        var resposta = controller.reativar(2, dto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(Status.ATIVO, resposta.getBody().getStatus());
        verify(service).reativar(2, dto);
    }

    @Test
    void Reativar_semDto_retornaOk() {
        CuidadorDTO reativado = cuidadorDTO();
        reativado.setStatus(Status.ATIVO);

        when(service.reativar(2, null)).thenReturn(reativado);

        var resposta = controller.reativar(2, null);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(Status.ATIVO, resposta.getBody().getStatus());
    }

    @Test
    void Reativar_cuidadorInexistente_lancaResourceNotFound() {
        when(service.reativar(99, null)).thenThrow(new ResourceNotFoundException("Cuidador", 99L));

        assertThrows(ResourceNotFoundException.class, () -> controller.reativar(99, null));
    }

    @Test
    void Deletar_cuidadorExistente_retornaNoContent() {
        var resposta = controller.deletar(2);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(service).inativar(2);
    }

    @Test
    void Deletar_cuidadorInexistente_lancaResourceNotFound() {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Cuidador", 99L))
                .when(service).inativar(99);

        assertThrows(ResourceNotFoundException.class, () -> controller.deletar(99));
    }

    @Test
    void Ping_requisicaoValida_retornaPong() {
        assertEquals("pong", controller.ping());
    }

    private CuidadorDTO cuidadorDTO() {
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
