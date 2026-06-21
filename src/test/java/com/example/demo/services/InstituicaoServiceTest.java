package com.example.demo.services;

import static com.example.demo.support.TestDataFactory.instituicaoAuth;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.entity.Instituicao;
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
