package com.example.demo.services;

import static com.example.demo.support.TestDataFactory.administrador;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dtos.AdministradorDTO;
import com.example.demo.entity.Administrador;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.AdministradorRepository;

@ExtendWith(MockitoExtension.class)
class AdministradorServiceTest {

    @Mock
    private AdministradorRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SenhaService senhaService;

    @Mock
    private EmailValidationService emailValidationService;

    @InjectMocks
    private AdministradorService service;

    @Test
    void deveLancarExcecaoAoCriarComCpfDuplicado() {
        AdministradorDTO dto = criarAdministradorDTO();

        when(repository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.cadastrarAdministrador(dto));
    }

    @Test
    void deveLancarExcecaoAoBuscarAdministradorInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarAdministradorPorId(99));
    }

    @Test
    void deveLancarExcecaoAoAtualizarAdministradorInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.atualizarAdministrador(99, criarAdministradorDTO()));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComCpfJaEmUso() {
        Administrador existente = administrador();
        existente.setCpf("00000000000");

        when(repository.findById(1)).thenReturn(Optional.of(existente));
        when(repository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.atualizarAdministrador(1, criarAdministradorDTO()));
    }

    @Test
    void deveLancarExcecaoAoInativarAdministradorInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.inativarAdministrador(99));
    }

    private AdministradorDTO criarAdministradorDTO() {
        AdministradorDTO dto = new AdministradorDTO();
        dto.setNome("Admin");
        dto.setCpf("12345678901");
        dto.setEmail("admin@email.com");
        dto.setSenha("Senha@123");
        return dto;
    }
}
