package com.example.demo.services;

import static com.example.demo.support.TestDataFactory.instituicaoAuth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.InstituicaoRepository;

@ExtendWith(MockitoExtension.class)
class InstituicaoServiceTest {

    @Mock
    private InstituicaoRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SenhaService senhaService;

    @Mock
    private EmailValidationService emailValidationService;

    @InjectMocks
    private InstituicaoService service;



    @Test
    void deveListarInstituicoesAtivasQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Instituicao> pagina = new PageImpl<>(List.of(instituicaoAuth()), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(pagina);

        Page<InstituicaoDTO> resultado = service.listarInstituicoesAtivas(null, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Instituicao", resultado.getContent().get(0).getNome());
        verify(repository).findAll(pageable);
    }

    @Test
    void deveCadastrarInstituicaoQuandoDadosForemValidos() {
        InstituicaoDTO dto = criarInstituicaoDTO();
        Instituicao salvo = instituicaoAuth();

        when(emailValidationService.validarEmailParaCriacao("instituicao@email.com")).thenReturn("instituicao@email.com");
        when(repository.existsByCnpj("12345678000199")).thenReturn(false);
        when(passwordEncoder.encode("Senha@123")).thenReturn("hash");
        when(repository.save(any(Instituicao.class))).thenReturn(salvo);

        InstituicaoDTO resultado = service.cadastrarInstituicao(dto);

        assertEquals(3, resultado.getId());
        assertEquals("Instituicao", resultado.getNome());
        verify(senhaService).validarSenha("Senha@123");
    }

    @Test
    void deveAtualizarInstituicaoQuandoDadosForemValidos() {
        Instituicao existente = instituicaoAuth();
        InstituicaoDTO dto = criarInstituicaoDTO();
        dto.setNome("Instituicao Atualizada");

        when(repository.findById(3)).thenReturn(Optional.of(existente));
        when(emailValidationService.validarEmailParaAtualizacao("instituicao@email.com", 3)).thenReturn("instituicao@email.com");
        when(passwordEncoder.encode("Senha@123")).thenReturn("nova-hash");
        when(repository.save(existente)).thenReturn(existente);

        InstituicaoDTO resultado = service.atualizarInstituicao(3, dto);

        assertEquals("Instituicao Atualizada", resultado.getNome());
        assertEquals("nova-hash", existente.getSenha());
        verify(repository).save(existente);
    }

    @Test
    void deveInativarInstituicaoQuandoInstituicaoExistir() {
        Instituicao existente = instituicaoAuth();

        when(repository.findById(3)).thenReturn(Optional.of(existente));

        service.inativarInstituicao(3);

        assertEquals(Status.INATIVO, existente.getStatus());
        verify(repository).save(existente);
    }

    @Test
    void deveReativarInstituicaoQuandoInstituicaoExistir() {
        Instituicao existente = instituicaoAuth();
        existente.setStatus(Status.INATIVO);

        when(repository.findById(3)).thenReturn(Optional.of(existente));

        service.reativarInstituicao(3);

        assertEquals(Status.ATIVO, existente.getStatus());
        verify(repository).save(existente);
    }

    @Test
    void deveLancarExcecaoAoCriarComCnpjDuplicado() {
        InstituicaoDTO dto = criarInstituicaoDTO();

        when(repository.existsByCnpj("12345678000199")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.cadastrarInstituicao(dto));
    }

    @Test
    void deveLancarExcecaoAoBuscarInstituicaoInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarInstituicaoPorId(99));
    }

    @Test
    void deveLancarExcecaoAoAtualizarInstituicaoInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.atualizarInstituicao(99, criarInstituicaoDTO()));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComCnpjJaEmUso() {
        Instituicao existente = instituicaoAuth();
        existente.setCnpj("00000000000000");

        when(repository.findById(3)).thenReturn(Optional.of(existente));
        when(repository.existsByCnpj("12345678000199")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.atualizarInstituicao(3, criarInstituicaoDTO()));
    }

    @Test
    void deveLancarExcecaoAoInativarInstituicaoInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.inativarInstituicao(99));
    }

    @Test
    void deveLancarExcecaoAoAtivarInstituicaoInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.reativarInstituicao(99));
    }

    private InstituicaoDTO criarInstituicaoDTO() {
        InstituicaoDTO dto = new InstituicaoDTO();
        dto.setNome("Instituicao Bom Cuidado");
        dto.setCnpj("12345678000199");
        dto.setEmail("instituicao@email.com");
        dto.setSenha("Senha@123");
        dto.setRua("Rua das Flores");
        dto.setBairro("Centro");
        dto.setUf("SP");
        dto.setNumero(100);
        dto.setCep("01310100");
        return dto;
    }
}
